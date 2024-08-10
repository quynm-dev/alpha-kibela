package alpha.service

import alpha.common.ServiceType
import alpha.common.UniResult
import alpha.data.dto.response.UserResponseDto
import alpha.data.`object`.UserObject
import alpha.error.AppError
import alpha.error.CodeFactory
import alpha.extension.isType
import alpha.extension.then
import alpha.extension.wrapError
import alpha.extension.wrapResult
import alpha.mapper.toResponse
import alpha.repository.UserRepository
import mu.KotlinLogging
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.koin.core.annotation.Singleton

private val logger = KotlinLogging.logger {}

@Singleton
class UserService(
    private val userRepository: UserRepository
) {
    suspend fun findAll(): UniResult<List<UserResponseDto>> {
        try {
            return userRepository.findAll().map { it.toResponse() }.wrapResult()
        } catch (e: ExposedSQLException) {
            logger.error { e.message }
            return AppError(CodeFactory.USER.DB_ERROR, "Failed to get all users").wrapError()
        } catch (e: Exception) {
            logger.error { e.message }
            return AppError(CodeFactory.USER.INTERNAL_SERVER_ERROR, "Unexpected error occurred").wrapError()
        }
    }

    suspend fun create(userObject: UserObject, isOAuthUser: Boolean? = true): UniResult<Int> {
        try {
            if (isOAuthUser == false) {
                findByUsername(userObject.username!!).then { e ->
                    if (!e.error.isType(CodeFactory.USER.NOT_FOUND)) {
                        logger.error { e.error.message }
                        return e
                    }
                }
            }

            return userRepository.create(userObject).wrapResult()
        } catch (e: ExposedSQLException) {
            logger.error { e.message }
            return AppError(CodeFactory.USER.DB_ERROR, "Failed to create a user").wrapError()
        } catch (e: Exception) {
            logger.error { e.message }
            return AppError(CodeFactory.USER.INTERNAL_SERVER_ERROR, "Unexpected error occurred").wrapError()
        }
    }

    suspend fun findById(id: Int): UniResult<UserObject> {
        try {
            val userObject = userRepository.findById(id)
            if (userObject == null) {
                logger.error { "User not found with id: $id" }
                return AppError(CodeFactory.USER.NOT_FOUND, "User not found").wrapError()
            }

            return userObject.wrapResult()
        } catch (e: ExposedSQLException) {
            logger.error { e.message }
            return AppError(CodeFactory.USER.DB_ERROR, "Failed to find a user with id: $id").wrapError()
        } catch (e: Exception) {
            logger.error { e.message }
            return AppError(CodeFactory.USER.INTERNAL_SERVER_ERROR, "Unexpected error occurred").wrapError()
        }
    }

    suspend fun findByUsername(username: String): UniResult<UserObject> {
        try {
            val userObject = userRepository.findByUsername(username)
            if (userObject == null) {
                logger.error { "User not found with username: $username" }
                return AppError(CodeFactory.USER.NOT_FOUND, "User not found").wrapError()
            }

            return userObject.wrapResult()
        } catch (e: ExposedSQLException) {
            logger.error { e.message }
            return AppError(CodeFactory.USER.DB_ERROR, "Failed to find a user with username: $username").wrapError()
        } catch (e: Exception) {
            logger.error { e.message }
            return AppError(CodeFactory.USER.INTERNAL_SERVER_ERROR, "Unexpected error occurred").wrapError()
        }
    }

    suspend fun findOAuthUserAndCreateIfNotExist(
        serviceType: ServiceType,
        newUserObject: UserObject
    ): UniResult<UserObject> {
        try {
            val sub = newUserObject.sub!!
            val userObject = userRepository.findOAuthUser(serviceType, sub)
            if (userObject == null) {
                val id = create(newUserObject).then {
                    logger.error { it.error }
                    return it
                }

                return findById(id)
            }

            return userObject.wrapResult()
        } catch (e: ExposedSQLException) {
            logger.error { e.message }
            return AppError(CodeFactory.USER.DB_ERROR, "Failed to find a user").wrapError()
        } catch (e: Exception) {
            logger.error { e.message }
            return AppError(CodeFactory.USER.INTERNAL_SERVER_ERROR, "Unexpected error occurred").wrapError()
        }
    }
}