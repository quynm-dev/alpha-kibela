package alpha.mapper

import alpha.common.Role
import alpha.common.ServiceType
import alpha.common.Status
import alpha.data.dto.response.UserResponseDto
import alpha.data.entity.UserEntity
import alpha.data.`object`.UserObject

fun UserEntity.toObject(): UserObject {
    return UserObject(
        id = this.id.value,
        username = this.username,
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