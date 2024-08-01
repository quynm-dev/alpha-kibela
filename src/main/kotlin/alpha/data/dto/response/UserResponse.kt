package alpha.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Int,
    val username: String,
    val email: String,
    val createdAt: String,
    val updatedAt: String
)