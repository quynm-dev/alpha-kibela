package alpha.mapper

import alpha.common.Role
import alpha.common.ServiceType
import alpha.common.Status
import alpha.data.dto.request.CreateUserRequestDto
import alpha.data.dto.response.UserResponseDto
import alpha.data.entity.UserEntity
import alpha.data.`object`.UserObject
import com.toxicbakery.bcrypt.Bcrypt

fun UserEntity.toObject(withPassword: Boolean? = false): UserObject {
    return UserObject(
        id = this.id.value,
        username = this.username,
        password = if (withPassword == true) this.password else null,
        name = this.name,
        email = this.email,
        imageUrl = this.imageUrl,
        sub = this.sub,
        serviceType = this.serviceType,
        role = this.role,
        status = this.status,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun CreateUserRequestDto.toObject(): UserObject {
    val salt = System.getenv("SALT") ?: throw IllegalStateException("Missing SALT environment variable")
    return UserObject(
        username = this.username,
        password = Bcrypt.hash(this.password, salt.toInt()).decodeToString(),
        name = this.name,
        email = this.email,
        imageUrl = this.imageUrl,
        role = this.role?.number ?: Role.USER.number,
        serviceType = ServiceType.STANDARD.number,
        status = Status.ACTIVE.number
    )
}

fun UserObject.toResponse(): UserResponseDto {
    return UserResponseDto(
        id = this.id!!,
        username = this.username,
        name = this.name,
        email = this.email,
        imageUrl = this.imageUrl,
        sub = this.sub,
        serviceType = ServiceType.from(this.serviceType),
        role = Role.from(this.role),
        status = Status.from(this.status),
        createdAt = this.createdAt!!,
        updatedAt = this.updatedAt!!
    )
}