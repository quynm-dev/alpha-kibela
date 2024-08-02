package alpha.config

import alpha.config.database.DatabaseConfig
import java.io.File

private const val MOCK_SQL_URL = "src/test/resources/mock.sql"
private const val H2_DRIVER = "org.h2.Driver"
private const val H2_URL = "jdbc:h2:mem:testdb"

private lateinit var transactionRunner: RepositoryTestRunner
private var count = 0

fun initH2Connection(): RepositoryTestRunner {
    val h2Database = H2Database(DatabaseConfig(jdbcUrl = H2_URL + count++, driver = H2_DRIVER))

    transactionRunner = RepositoryTestRunner(h2Database.initDataSource(), h2Database.db) {
        val mockSQLFile = File(MOCK_SQL_URL)
        if (mockSQLFile.exists()) {
            exec(mockSQLFile.readText())
        }
    }

    return transactionRunner
}