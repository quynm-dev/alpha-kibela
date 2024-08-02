package alpha.mapper

import alpha.data.dto.response.UserResponse
import alpha.data.entity.UserEntity
import alpha.data.`object`.UserObject

fun UserEntity.toObject(): UserObject {
    return UserObject(
        id = this.id.value,
        username = this.username,
        email = this.email,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt)
}

fun UserObject.toResponse(): UserResponse {
    return UserResponse(
        id = this.id,
        username = this.username,
        email = this.email,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt)
}
