package alpha.mapper

import alpha.data.dto.response.GoogleUserInfoResponse
import alpha.data.`object`.UserObject

fun GoogleUserInfoResponse.toUserObject(): UserObject {
    return UserObject(
        name = this.name,
        email = this.email,
        imageUrl = this.picture,
        sub = this.sub
    )
}