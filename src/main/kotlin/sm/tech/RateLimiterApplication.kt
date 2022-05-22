package sm.tech

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import sm.tech.config.RedisConfig
import java.time.Duration

@SpringBootApplication
class RateLimiterApplication {

    @Bean
    @ConfigurationProperties(prefix = "redis")
    fun redisConfig() = RedisConfig()

    @Bean
    fun jedisPoolConfig(redisConfig: RedisConfig): JedisPoolConfig {
        val poolConfig = JedisPoolConfig()
        poolConfig.maxTotal = redisConfig.poolConfig!!.maxTotal
        poolConfig.maxIdle = redisConfig.poolConfig!!.maxIdle
        poolConfig.minIdle = redisConfig.poolConfig!!.minIdle
        poolConfig.testOnBorrow = true
        poolConfig.testOnReturn = true
        poolConfig.testWhileIdle = true
        poolConfig.minEvictableIdleTimeMillis = Duration.ofSeconds(60).toMillis()
        poolConfig.timeBetweenEvictionRunsMillis = Duration.ofSeconds(30).toMillis()
        poolConfig.numTestsPerEvictionRun = 3
        poolConfig.blockWhenExhausted = true
        return poolConfig
    }

    @Bean(destroyMethod = "close")
    fun jedisPool(jedisPoolConfig: JedisPoolConfig, redisConfig: RedisConfig): JedisPool {
        if (redisConfig.password.isNullOrBlank()) {
            return JedisPool(jedisPoolConfig, redisConfig.host, redisConfig.port!!, redisConfig.timeout!!)
        }
        return JedisPool(jedisPoolConfig, redisConfig.host, redisConfig.port!!, redisConfig.timeout!!, redisConfig.password)
    }

}

fun main(args: Array<String>) {
    runApplication<RateLimiterApplication>(*args)
}
