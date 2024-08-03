package alpha.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val code: String
)

@Serializable
data class GoogleAuthRequest(
    @SerialName("client_id")
    val clientId: String,
    @SerialName("client_secret")
    val clientSecret: String,
    @SerialName("code")
    val code: String,
    @SerialName("redirect_uri")
    val redirectUri: String,
    @SerialName("grant_type")
    val grantType: String
)