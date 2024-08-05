package alpha.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StandardAuthRequestDto(
    val username: String,
    val password: String
)

@Serializable
data class OAuthRequestDto(
    val code: String
)

@Serializable
data class GoogleAuthRequestDto(
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

@Serializable
data class FacebookAuthRequestDto(
    @SerialName("client_id")
    val clientId: String,
    @SerialName("client_secret")
    val clientSecret: String,
    @SerialName("code")
    val code: String,
    @SerialName("redirect_uri")
    val redirectUri: String
)