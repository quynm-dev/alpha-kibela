package alpha.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import liquibase.Scope
import liquibase.command.CommandScope
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.jetbrains.exposed.sql.Database

data class DatabaseConfig(
    val driver: String,
    val jdbcUrl: String,
    val user: String,
    val password: String,
    val name: String
)

class DatabaseConfigurer(private val config: DatabaseConfig) {
    companion object {
        const val CHANGELOG_FILE = "db/changelog.yaml"
    }

    fun initConnection() {
        val dataSource = initDataSource(config)
        Database.connect(dataSource)

        Scope.child(Scope.Attr.resourceAccessor, ClassLoaderResourceAccessor()) {
            CommandScope("update")
                .addArgumentValue("changelogFile", CHANGELOG_FILE)
                .addArgumentValue("database", DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(dataSource.connection)))
                .execute()
        }
    }

    private fun initDataSource(config: DatabaseConfig) = HikariDataSource(initHikariConfig(config))

    private fun initHikariConfig(config: DatabaseConfig): HikariConfig {
        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl = "${config.jdbcUrl}/${config.name}"
        hikariConfig.username = config.user
        hikariConfig.password = config.password
        hikariConfig.driverClassName = config.driver
        hikariConfig.isAutoCommit = true
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true")
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250")
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

        return hikariConfig
    }
}

fun Application.configureDatabase() {
    val config = DatabaseConfig(
        driver = System.getenv("MYSQL_DRIVER") ?: throw IllegalStateException("Missing MYSQL_DRIVER environment variable"),
        jdbcUrl = System.getenv("MYSQL_URL") ?: throw IllegalStateException("Missing MYSQL_URL environment variable"),
        user = System.getenv("MYSQL_USER") ?: throw IllegalStateException("Missing MYSQL_USER environment variable"),
        password = System.getenv("MYSQL_PASSWORD") ?: throw IllegalStateException("Missing MYSQL_PASSWORD environment variable"),
        name = System.getenv("MYSQL_DATABASE") ?: throw IllegalStateException("Missing MYSQL_DATABASE environment variable")
    )

    DatabaseConfigurer(config).initConnection()
}