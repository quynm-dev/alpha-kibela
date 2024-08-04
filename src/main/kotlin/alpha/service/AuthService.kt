package alpha.service

import alpha.common.UniResult
import alpha.config.Redis
import alpha.config.httpClient.HttpClient
import alpha.data.dto.request.AuthRequest
import alpha.data.dto.request.GoogleAuthRequest
import alpha.data.dto.response.AuthResponse
import alpha.data.dto.response.GoogleAuthResponse
import alpha.data.dto.response.GoogleUserInfoResponse
import alpha.data.`object`.UserObject
import alpha.error.AppError
import alpha.error.CodeFactory
import alpha.extension.deserializeWithStatus
import alpha.extension.then
import alpha.extension.wrapError
import alpha.extension.wrapResult
import alpha.mapper.toUserObject
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import mu.KotlinLogging
import org.koin.core.annotation.Singleton
import java.time.Instant
import java.util.*

private val logger = KotlinLogging.logger {}

@Singleton
class AuthService(
    private val userService: UserService,
    private val redis: Redis,
    private val httpClient: HttpClient
) {
    companion object {
        const val AUTH_URL = "oauth2.googleapis.com/token"
        const val USER_INFO_URL = "www.googleapis.com/oauth2/v3/userinfo"
        const val GRANT_TYPE = "authorization_code"
        const val JWT_ISSUER = "alpha-kibela"
        const val JWT_ACCESS_TOKEN_AUDIENCE = "alpha-kibela-access-token"
        const val JWT_REFRESH_TOKEN_AUDIENCE = "alpha-kibela-refresh-token"
        const val JWT_ACCESS_TOKEN_EXPIRATION_TIME: Long = 60 * 60 * 1
        const val JWT_REFRESH_TOKEN_EXPIRATION_TIME: Long = 60 * 60 * 24 * 30
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
            val userObject = userService.findBySub(googleUserInfoResponse.sub).then {
                userService.create(googleUserInfoResponse.toUserObject()).then { e ->
                    logger.error { e.error }
                    return e
                }
            }
            val (accessToken, refreshToken) = generateTokenPair(userObject as UserObject)

            storeRedisData(
                userObject.id.toString(), userObject.sub, accessToken, refreshToken, googleAuthResponse.accessToken,
                googleAuthResponse.idToken
            )

            val authResponse = AuthResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresIn = JWT_ACCESS_TOKEN_EXPIRATION_TIME.toInt()
            )

            return authResponse.wrapResult()
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

        return httpClient.post(AUTH_URL, googleAuthRequest)
            .deserializeWithStatus<GoogleAuthResponse>(HttpStatusCode.OK) {
                return AppError(CodeFactory.USER.UNAUTHORIZED, "Unauthorized").wrapError()
            }.wrapResult()
    }

    private suspend fun getUserInfoGoogle(accessToken: String): UniResult<GoogleUserInfoResponse> {
        val googleUserInfoResponse = httpClient.get(USER_INFO_URL) {
            authorization = "Bearer $accessToken"
        }

        return googleUserInfoResponse.deserializeWithStatus<GoogleUserInfoResponse>(HttpStatusCode.OK) {
            return AppError(CodeFactory.USER.INTERNAL_SERVER_ERROR, "Failed to get google user info").wrapError()
        }.wrapResult()
    }

    private fun generateTokenPair(userObject: UserObject): Pair<String, String> {
        val secret =
            System.getenv("JWT_SECRET") ?: throw IllegalStateException("Missing JWT_SECRET environment variable")
        val template = JWT.create()
            .withIssuer(JWT_ISSUER)
            .withClaim("id", userObject.id)
            .withClaim("name", userObject.name)
            .withClaim("email", userObject.email)
            .withIssuedAt(Date())
        val accessToken = template
            .withAudience(JWT_ACCESS_TOKEN_AUDIENCE)
            .withExpiresAt(Date.from(Instant.now().plusSeconds(JWT_ACCESS_TOKEN_EXPIRATION_TIME)))
            .sign(Algorithm.HMAC256(secret))
        val refreshToken = template
            .withAudience(JWT_REFRESH_TOKEN_AUDIENCE)
            .withExpiresAt(Date.from(Instant.now().plusSeconds(JWT_REFRESH_TOKEN_EXPIRATION_TIME)))
            .sign(Algorithm.HMAC256(secret))

        return Pair(accessToken, refreshToken)
    }

    private suspend fun storeRedisData(
        id: String,
        sub: String,
        accessToken: String,
        refreshToken: String,
        googleAccessToken: String,
        googleIdToken: String
    ) {
        try {
            val keyPrefix = "user:$id"

            redis.write(keyPrefix, id)
            redis.write("$keyPrefix:sub", sub)
            redis.write("$keyPrefix:accessToken", accessToken)
            redis.write("$keyPrefix:refreshToken", refreshToken)
            redis.write("$keyPrefix:googleAccessToken", googleAccessToken)
            redis.write("$keyPrefix:googleIdToken", googleIdToken)
        } catch (e: Exception) {
            logger.error { e.message }
            throw e
        }
    }
}