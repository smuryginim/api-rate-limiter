package sm.tech.ratelimit

/**
 * This annotation can be used on controller method to enable access throttling
 * Response type should be enclosed with @class org.springframework.http.ResponseEntity<>
 * @args withException - can be use
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RateLimit(
    val withException: Boolean = false,
    val maxRatePerMinute: Int = 60
)
