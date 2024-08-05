package alpha.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int
)

@Serializable
data class GoogleAuthResponseDto(
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    val scope: String,
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("id_token")
    val idToken: String
)

@Serializable
data class GoogleUserInfoResponseDto(
    val sub: String,
    val name: String,
    @SerialName("given_name")
    val givenName: String,
    val picture: String,
    val email: String,
    @SerialName("email_verified")
    val emailVerified: Boolean,
)

@Serializable
data class FacebookAuthResponseDto(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("expires_in")
    val expiresIn: Int
)

@Serializable
data class FacebookUserInfoResponseDto(
    val id: String,
    val name: String,
    val picture: FacebookPictureResponseDto,
    val email: String
)

@Serializable
data class FacebookPictureResponseDto(
    val data: FacebookPictureDataResponseDto
)

@Serializable
data class FacebookPictureDataResponseDto(
    val height: Int,
    @SerialName("is_silhouette")
    val isSilhouette: Boolean,
    val url: String,
    val width: Int
)