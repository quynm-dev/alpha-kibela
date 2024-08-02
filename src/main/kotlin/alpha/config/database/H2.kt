package alpha.config.database

class H2Database(config: DatabaseConfig): IDatabase(
    config.copy(autoCommit = false)
) {
    override val url = "${config.jdbcUrl}/${config.name};MODE=MYSQL;DATABASE_TO_LOWER=TRUE;"
}