package alpha.data.`object`

import java.time.LocalDateTime

data class UserObject(
    val id: Int,
    val username: String,
    val email: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
