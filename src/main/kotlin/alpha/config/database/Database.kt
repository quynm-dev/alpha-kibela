package alpha.config.database

import io.ktor.server.application.*

fun Application.configureDatabase() {
    MySQLDatabase(DatabaseConfig()).initDataSource()
}

data class DatabaseConfig(
    val driver: String =
        System.getenv("MYSQL_DRIVER")
            ?: throw IllegalStateException("Missing MYSQL_DRIVER environment variable"),
    val jdbcUrl: String =
        System.getenv("MYSQL_URL")
            ?: throw IllegalStateException("Missing MYSQL_URL environment variable"),
    val user: String =
        System.getenv("MYSQL_USER")
            ?: throw IllegalStateException("Missing MYSQL_USER environment variable"),
    val password: String =
        System.getenv("MYSQL_PASSWORD")
            ?: throw IllegalStateException("Missing MYSQL_PASSWORD environment variable"),
    val name: String =
        System.getenv("MYSQL_DATABASE")
            ?: throw IllegalStateException("Missing MYSQL_DATABASE environment variable"),
    val autoCommit: Boolean = true
)
