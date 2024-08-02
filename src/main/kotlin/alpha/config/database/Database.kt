package alpha.config.database

import io.ktor.server.application.*

fun Application.configureDatabase() {
    val config = DatabaseConfig(
        driver = System.getenv("MYSQL_DRIVER") ?: throw IllegalStateException("Missing MYSQL_DRIVER environment variable"),
        jdbcUrl = System.getenv("MYSQL_URL") ?: throw IllegalStateException("Missing MYSQL_URL environment variable"),
        user = System.getenv("MYSQL_USER") ?: throw IllegalStateException("Missing MYSQL_USER environment variable"),
        password = System.getenv("MYSQL_PASSWORD") ?: throw IllegalStateException("Missing MYSQL_PASSWORD environment variable"),
        name = System.getenv("MYSQL_DATABASE") ?: throw IllegalStateException("Missing MYSQL_DATABASE environment variable"),
        autoCommit = true
    )

    MySQLDatabase(config).initConnection()
}

data class DatabaseConfig(
    val driver: String,
    val jdbcUrl: String,
    val user: String,
    val password: String,
    val name: String,
    val autoCommit: Boolean
)