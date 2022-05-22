package sm.tech.ratelimit

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import redis.clients.jedis.JedisPool
import redis.clients.jedis.exceptions.JedisConnectionException
import sm.tech.exception.ResponseError
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.naming.LimitExceededException
import javax.servlet.http.HttpServletRequest

@Aspect
@Component
class RateLimitAspect {

    @Autowired
    lateinit var jedisPool: JedisPool

    companion object {
        private val logger = LoggerFactory.getLogger(RateLimitAspect::class.java)
        private const val ERROR_MESSAGE = "Limit for clientId = '%s' is greater than allowed (%d)"
    }

    @Around("@annotation(rateLimit)")
    fun rateLimitWithResponse(joinPoint: ProceedingJoinPoint, rateLimit: RateLimit): Any? {
        val args = joinPoint.args

        val request: HttpServletRequest = try {
            args.filterIsInstance<HttpServletRequest>().first()
        } catch (e: Exception) {
            // Grant possibility to main logic to decide what to do
            logger.warn("Rate limiter advice: Couldn't extract request information, no rate limit will be applied.")
            return joinPoint.proceed(args)
        }

        val ratePerMinute = rateLimit.maxRatePerMinute
        val remoteAddress = request.remoteAddr

        val withException = rateLimit.withException
        if (isRateExceeded(remoteAddress, ratePerMinute)) {
            if (withException) {
                throw LimitExceededException("Limit for repoAccessId = '$remoteAddress' is greater than allowed ($ratePerMinute)")
            } else {
                return generateRateLimitResponse(remoteAddress, ratePerMinute)
            }
        }

        return joinPoint.proceed(args)
    }

    private fun isRateExceeded(clientId: String, ratePerMinute: Int): Boolean {
        val redisKeyToMinute = clientId + ':' + LocalDateTime.now().minute

        try {
            //Used algorithm from https://redislabs.com/redis-best-practices/basic-rate-limiting/
            jedisPool.resource.use { jedis ->
                val count = Integer.valueOf(jedis.get(redisKeyToMinute) ?: "0")

                if (count == ratePerMinute) {
                    return true
                }

                val multi = jedis.multi()
                multi.incr(redisKeyToMinute)
                multi.expire(redisKeyToMinute, 59)
                multi.exec()
            }
        } catch (ex: JedisConnectionException) {
            //This is implemented in such way, because if there will be some issues with Redis - Rate limiting will be disabled
            logger.warn("Problem with connection to Redis", ex)
        }

        return false
    }

    private fun generateRateLimitResponse(clientId: String, ratePerMinute: Int): ResponseEntity<ResponseError> {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request

        val timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        val headers = HttpHeaders()
        headers.add(HttpHeaders.RETRY_AFTER, "${60 - LocalDateTime.now().second}")

        return ResponseEntity
            .status(HttpStatus.TOO_MANY_REQUESTS)
            .headers(headers)
            .body(ResponseError(timestamp, request.requestURI, HttpStatus.TOO_MANY_REQUESTS.value(), LimitExceededException::class.java.simpleName,
                ERROR_MESSAGE.format(clientId, ratePerMinute)))
    }
}
