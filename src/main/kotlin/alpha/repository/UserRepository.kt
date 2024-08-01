package alpha.repository

import alpha.data.entity.UserEntity
import alpha.data.entity.Users
import alpha.data.`object`.UserObject
import alpha.helper.transactionWrapper
import alpha.mapper.toObject
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.selectAll
import org.koin.core.annotation.Singleton

@Singleton
class UserRepository {
    suspend fun getAll(): List<UserObject> {
        return transactionWrapper {
            try {
                Users.selectAll().map { UserEntity.wrapRow(it).toObject() }
            } catch (e: ExposedSQLException) {
                throw e
            }
        }
    }

    suspend fun create() {
        println("UserRepository.create")
    }

    suspend fun get() {
        println("UserRepository.read")
    }

    suspend fun update() {
        println("UserRepository.update")
    }

    suspend fun delete() {
        println("UserRepository.delete")
    }
}