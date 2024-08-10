package alpha.config.database

import alpha.helper.getEnvOrError
import io.ktor.server.application.*

fun Application.configureDatabase() {
    MySQLDatabase(DatabaseConfig()).initDataSource()
}

data class DatabaseConfig(
    val driver: String = getEnvOrError("MYSQL_DRIVER"),
    val jdbcUrl: String = getEnvOrError("MYSQL_URL"),
    val user: String = getEnvOrError("MYSQL_USER"),
    val password: String = getEnvOrError("MYSQL_PASSWORD"),
    val name: String = getEnvOrError("MYSQL_DATABASE"),
    val autoCommit: Boolean = true
)