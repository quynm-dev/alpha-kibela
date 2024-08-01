package alpha.error

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val code: String, val message: String)

data class AppError(val code: Code, val message: String) {
    fun toResponse() = ErrorResponse("${code.domain.symbol}-${code.status.value}", message)
}