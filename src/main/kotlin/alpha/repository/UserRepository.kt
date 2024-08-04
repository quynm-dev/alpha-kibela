package alpha.repository

import alpha.data.entity.UserEntity
import alpha.data.entity.Users
import alpha.data.`object`.UserObject
import alpha.helper.transactionWrapper
import alpha.mapper.toObject
import mu.KotlinLogging
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.koin.core.annotation.Singleton
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Singleton
class UserRepository {
    suspend fun findAll(): List<UserObject> {
        return transactionWrapper {
            try {
                Users.selectAll().map { UserEntity.wrapRow(it).toObject() }
            } catch (e: ExposedSQLException) {
                logger.error { e.message }
                throw e
            }
        }
    }

    suspend fun create(userObject: UserObject): Int {
        return transactionWrapper {
            try {
                Users.insert {
                    it[name] = userObject.name
                    it[email] = userObject.email
                    it[imageUrl] = userObject.imageUrl
                    it[sub] = userObject.sub
                    it[createdAt] = LocalDateTime.now()
                    it[updatedAt] = LocalDateTime.now()
                }[Users.id].value
            } catch (e: ExposedSQLException) {
                logger.error { e.message }
                throw e
            }
        }
    }

    suspend fun findBySub(sub: String): UserObject? {
        return transactionWrapper {
            try {
                UserEntity.find { Users.sub eq sub }.firstOrNull()?.toObject()
            } catch (e: ExposedSQLException) {
                logger.error { e.message }
                throw e
            }
        }
    }
}