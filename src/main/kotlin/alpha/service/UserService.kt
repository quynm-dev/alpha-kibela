package alpha.service

import alpha.common.UniResult
import alpha.data.dto.response.UserResponse
import alpha.error.AppError
import alpha.error.CodeFactory
import alpha.extension.wrapError
import alpha.extension.wrapResult
import alpha.mapper.toResponse
import alpha.repository.UserRepository
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.koin.core.annotation.Singleton

@Singleton
class UserService(
    private val userRepository: UserRepository
) {
    suspend fun getAll(): UniResult<List<UserResponse>> {
        try {
            return userRepository.getAll().map { it.toResponse() }.wrapResult()
        } catch (e: ExposedSQLException) {
            val appErr = AppError(CodeFactory.USER.DB_ERROR, "Failed to get all users")
            return appErr.wrapError()
        } catch (e: Exception) {
            val appErr = AppError(CodeFactory.GENERAL.INTERNAL_SERVER_ERROR, "Unexpected error occurred")
            return appErr.wrapError()
        }
    }
}