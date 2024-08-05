package alpha.data.dto.response

import alpha.common.Role
import alpha.common.ServiceType
import alpha.common.Status
import alpha.util.serializer.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class UserResponseDto(
    val id: Int,
    val username: String?,
    val name: String,
    val email: String?,
    val imageUrl: String?,
    val sub: String?,
    val serviceType: ServiceType,
    val role: Role,
    val status: Status,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime
)