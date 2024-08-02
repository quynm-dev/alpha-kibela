package alpha.config.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import liquibase.Scope
import liquibase.command.CommandScope
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.jetbrains.exposed.sql.Database

abstract class IDatabase(val config: DatabaseConfig) {
    companion object {
        const val CHANGELOG_FILE = "db/changelog.yaml"
    }

    lateinit var db: Database
    abstract val url: String

    fun initDataSource(): HikariDataSource {
        val dataSource = HikariDataSource(initHikariConfig(config))
        db = Database.connect(dataSource)

        Scope.child(Scope.Attr.resourceAccessor, ClassLoaderResourceAccessor()) {
            CommandScope("update")
                .addArgumentValue("changelogFile", CHANGELOG_FILE)
                .addArgumentValue("database", DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
                    JdbcConnection(dataSource.connection)
                ))
                .execute()
        }

        return dataSource
    }

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