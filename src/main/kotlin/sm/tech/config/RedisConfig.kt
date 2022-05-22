package sm.tech.config

import redis.clients.jedis.JedisPoolConfig

open class RedisConfig {
    var host: String? = null
    var port: Int? = null
    var timeout: Int? = null
    var password: String? = null
    var poolConfig: JedisPoolConfig? = null
}
