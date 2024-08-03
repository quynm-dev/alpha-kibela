package alpha.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val accessToken: String,
    val idToken: String,
    val expiresIn: Int
)

@Serializable
data class GoogleAuthResponse(
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
data class GoogleUserInfoResponse(
    val sub: String,
    val name: String,
    @SerialName("given_name")
    val givenName: String,
    val picture: String,
    val email: String,
    @SerialName("email_verified")
    val emailVerified: Boolean,
)