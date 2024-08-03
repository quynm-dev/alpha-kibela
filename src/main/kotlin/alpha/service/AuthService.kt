package alpha.service

import alpha.common.UniResult
import alpha.config.httpClient.HttpClient
import alpha.data.dto.request.AuthRequest
import alpha.data.dto.request.GoogleAuthRequest
import alpha.data.dto.response.AuthResponse
import alpha.data.dto.response.GoogleAuthResponse
import alpha.data.dto.response.GoogleUserInfoResponse
import alpha.error.AppError
import alpha.error.CodeFactory
import alpha.extension.deserializeWithStatus
import alpha.extension.then
import alpha.extension.wrapError
import alpha.extension.wrapResult
import io.ktor.http.*
import mu.KotlinLogging
import org.koin.core.annotation.Singleton

val logger = KotlinLogging.logger {}

@Singleton
class AuthService(
    private val httpClient: HttpClient
) {
    companion object {
        private const val AUTH_URL = "oauth2.googleapis.com/token"
        private const val USER_INFO_URL = "www.googleapis.com/oauth2/v3/userinfo"
        private const val GRANT_TYPE = "authorization_code"
    }

    suspend fun authenticate(authRequest: AuthRequest): UniResult<AuthResponse> {
        try {
            val googleAuthResponse = authenticateGoogle(authRequest).then {
                logger.error { it.error }
                return it
            }
            val googleUserInfoResponse = getUserInfoGoogle(googleAuthResponse.accessToken).then {
                logger.error { it.error }
                return it
            }
            // check user existence and insert a new record if not found

            // generate token pair

            // store token pair in redis
            val authReponse = AuthResponse(
                accessToken = googleAuthResponse.accessToken,
                idToken = googleAuthResponse.idToken,
                expiresIn = googleAuthResponse.expiresIn
            )

            return authReponse.wrapResult()
        } catch (e: Exception) {
            val appErr = AppError(CodeFactory.USER.INTERNAL_SERVER_ERROR, "Unexpected error occurred")
            return appErr.wrapError()
        }
    }

    private suspend fun authenticateGoogle(authRequest: AuthRequest): UniResult<GoogleAuthResponse> {
        val clientId = System.getenv("CLIENT_ID")
            ?: throw IllegalStateException("Missing CLIENT_ID environment variable")
        val clientSecret = System.getenv("CLIENT_SECRET")
            ?: throw IllegalStateException("Missing CLIENT_SECRET environment variable")
        val redirectUri = System.getenv("REDIRECT_URI")
            ?: throw IllegalStateException("Missing REDIRECT_URI environment variable")
        val googleAuthRequest = GoogleAuthRequest(
            code = authRequest.code,
            clientId = clientId,
            clientSecret = clientSecret,
            redirectUri = redirectUri,
            grantType = GRANT_TYPE
        )

        return httpClient.post(AUTH_URL, googleAuthRequest).deserializeWithStatus<GoogleAuthResponse>(HttpStatusCode.OK) {
            return AppError(CodeFactory.USER.UNAUTHORIZED, "Unauthorized").wrapError()
        }.wrapResult()
    }

    private suspend fun getUserInfoGoogle(accessToken: String): UniResult<GoogleUserInfoResponse> {
        val googleUserInfoResponse =  httpClient.get(USER_INFO_URL) {
            authorization = "Bearer $accessToken"
        }

        return googleUserInfoResponse.deserializeWithStatus<GoogleUserInfoResponse>(HttpStatusCode.OK) {
            return AppError(CodeFactory.USER.INTERNAL_SERVER_ERROR, "Failed to get google user info").wrapError()
        }.wrapResult()
    }
}