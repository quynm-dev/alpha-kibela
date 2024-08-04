package alpha.mapper

import alpha.data.dto.response.UserResponse
import alpha.data.entity.UserEntity
import alpha.data.`object`.UserObject

fun UserEntity.toObject(): UserObject {
    return UserObject(
        id = this.id.value,
        name = this.name,
        email = this.email,
        imageUrl = this.imageUrl,
        sub = this.sub,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun UserObject.toResponse(): UserResponse {
    return UserResponse(
        id = this.id!!,
        name = this.name,
        email = this.email,
        imageUrl = this.imageUrl,
        sub = this.sub,
        createdAt = this.createdAt!!,
        updatedAt = this.updatedAt!!
    )
}