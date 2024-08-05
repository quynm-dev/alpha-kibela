package alpha.data.`object`

import java.time.LocalDateTime

data class UserObject(
    val id: Int? = null,
    val username: String? = null,
    val password: String? = null,
    val name: String,
    val email: String? = null,
    val imageUrl: String? = null,
    val sub: String? = null,
    val serviceType: Byte,
    val role: Byte,
    val status: Byte,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)