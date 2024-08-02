package alpha.config

import alpha.config.database.DatabaseConfig
import alpha.config.database.IDatabase

class H2Database(config: DatabaseConfig) : IDatabase(config.copy(autoCommit = false)) {
    override val url = "${config.jdbcUrl}/${config.name};MODE=MYSQL;DATABASE_TO_LOWER=TRUE;"
}
