package alpha.mapper

import alpha.common.Role
import alpha.common.ServiceType
import alpha.common.Status
import alpha.data.dto.response.GoogleUserInfoResponseDto
import alpha.data.`object`.UserObject

fun GoogleUserInfoResponseDto.toUserObject(): UserObject {
    return UserObject(
        name = this.name,
        email = this.email,
        imageUrl = this.picture,
        sub = this.sub,
        serviceType = ServiceType.GOOGLE.number,
        role = Role.USER.number,
        status = Status.ACTIVE.number
    )
}