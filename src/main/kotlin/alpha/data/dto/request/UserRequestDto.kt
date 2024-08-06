package alpha.data.dto.request

import alpha.common.Role
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequestDto(
    val username: String,
    val password: String,
    val name: String,
    val email: String? = null,
    val imageUrl: String? = null,
    val role: Role? = Role.USER
)