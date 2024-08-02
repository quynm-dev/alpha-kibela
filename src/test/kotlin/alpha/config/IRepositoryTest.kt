package alpha.config

import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction

class RepositoryTestRunner(
    private val dataSource: HikariDataSource,
    private val db: Database,
    initBlock: Transaction.() -> Unit
) {
    init {
        transaction(db) { initBlock() }
    }

    operator fun invoke(block: suspend () -> Unit) {
        transaction(db) {
            runBlocking { block() }
            rollback()
        }
    }

    fun close() {
        dataSource.close()
    }
}

abstract class IRepositoryTest {
    lateinit var repoRunner: RepositoryTestRunner

    fun init() {
        repoRunner = initH2Connection()
    }

    fun finish() {
        repoRunner.close()
    }
}
