package alpha.config.database

class MySQLDatabase(config: DatabaseConfig) : IDatabase(config) {
    override val url =
        "${config.jdbcUrl}/${config.name}?allowMultiQueries=true&enabledTLSProtocols=TLSv1.2;"
}
