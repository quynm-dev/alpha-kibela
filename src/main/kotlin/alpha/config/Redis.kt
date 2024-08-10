package alpha.config

import alpha.helper.getEnv
import alpha.helper.getEnvOrDefaultInt
import alpha.helper.getEnvOrError
import io.ktor.server.sessions.*
import mu.KotlinLogging
import org.koin.core.annotation.Singleton
import redis.clients.jedis.DefaultJedisClientConfig
import redis.clients.jedis.HostAndPort
import redis.clients.jedis.JedisPool

private val logger = KotlinLogging.logger {}

@Singleton
class Redis : SessionStorage {
    companion object {
        private val KEY_PREFIX = "stark:"
        private val EXPIRATION_TIME: Long = 60 * 60 * 24 * 30
    }

    private val jedisPool: JedisPool

    init {
        val host = getEnvOrError("REDIS_HOST")
        val timeout = getEnvOrDefaultInt("REDIS_TIMEOUT", 2000)
        val password = getEnv("REDIS_PASSWORD")
        val config = DefaultJedisClientConfig.builder()
            .ssl(false)
            .user("default")
            .password(password)
            .timeoutMillis(timeout)
            .build()
        jedisPool = JedisPool(HostAndPort(host, 6379), config)
        jedisPool.resource
        logger.info { "Redis connected" }
    }

    override suspend fun read(id: String): String {
        val key = KEY_PREFIX + id
        jedisPool.resource.use { jedis ->
            jedis[key]?.let { return it } ?: throw IllegalStateException("Session $key not found")
        }
    }

    override suspend fun write(id: String, value: String) {
        val key = KEY_PREFIX + id
        jedisPool.resource.use { jedis ->
            jedis.set(key, value)
            jedis.expire(key, EXPIRATION_TIME)
        }
    }

    override suspend fun invalidate(id: String) {
        val key = KEY_PREFIX + id
        jedisPool.resource.use { jedis ->
            if (jedis[key].isNullOrEmpty()) {
                throw IllegalStateException("Session $key not found")
            }

            jedis.del(key)
        }
    }
}