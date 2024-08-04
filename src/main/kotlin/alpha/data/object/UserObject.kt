package alpha.data.`object`

import java.time.LocalDateTime

data class UserObject(
    val id: Int? = null,
    val name: String,
    val email: String,
    val imageUrl: String,
    val sub: String,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)