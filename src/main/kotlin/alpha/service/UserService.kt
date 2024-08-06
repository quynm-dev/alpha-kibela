package alpha.service

import alpha.common.ServiceType
import alpha.common.UniResult
import alpha.data.dto.response.UserResponseDto
import alpha.data.`object`.UserObject
import alpha.error.AppError
import alpha.error.CodeFactory
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
            val appErr = AppError(CodeFactory.USER.DB_ERROR, "Failed to get all users")
            return appErr.wrapError()
        } catch (e: Exception) {
            val appErr = AppError(CodeFactory.USER.INTERNAL_SERVER_ERROR, "Unexpected error occurred")
            return appErr.wrapError()
        }
    }

    suspend fun create(userObject: UserObject): UniResult<Int> {
        try {
            return userRepository.create(userObject).wrapResult()
        } catch (e: ExposedSQLException) {
            val appErr = AppError(CodeFactory.USER.DB_ERROR, "Failed to create a user")
            return appErr.wrapError()
        } catch (e: Exception) {
            val appErr = AppError(CodeFactory.USER.INTERNAL_SERVER_ERROR, "Unexpected error occurred")
            return appErr.wrapError()
        }
    }

    suspend fun findById(id: Int): UniResult<UserObject> {
        try {
            val userObject = userRepository.findById(id)
            if (userObject == null) {
                val notFoundErr = AppError(CodeFactory.USER.NOT_FOUND, "User not found")
                return notFoundErr.wrapError()
            }

            return userObject.wrapResult()
        } catch (e: ExposedSQLException) {
            val appErr = AppError(CodeFactory.USER.DB_ERROR, "Failed to find a user with id: $id")
            return appErr.wrapError()
        } catch (e: Exception) {
            val appErr = AppError(CodeFactory.USER.INTERNAL_SERVER_ERROR, "Unexpected error occurred")
            return appErr.wrapError()
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
            val appErr = AppError(CodeFactory.USER.DB_ERROR, "Failed to find a user")
            return appErr.wrapError()
        } catch (e: Exception) {
            val appErr = AppError(CodeFactory.USER.INTERNAL_SERVER_ERROR, "Unexpected error occurred")
            return appErr.wrapError()
        }
    }
}