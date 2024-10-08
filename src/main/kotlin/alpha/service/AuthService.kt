package alpha.service

import alpha.common.ServiceType
import alpha.common.UniResult
import alpha.config.Redis
import alpha.config.httpClient.HttpClient
import alpha.data.dto.request.FacebookAuthRequestDto
import alpha.data.dto.request.GoogleAuthRequestDto
import alpha.data.dto.request.OAuthRequestDto
import alpha.data.dto.request.StandardAuthRequestDto
import alpha.data.dto.response.*
import alpha.data.`object`.UserObject
import alpha.error.AppError
import alpha.error.CodeFactory
import alpha.extension.deserializeWithStatus
import alpha.extension.then
import alpha.extension.wrapError
import alpha.extension.wrapResult
import alpha.helper.getEnvOrError
import alpha.mapper.toUserObject
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.toxicbakery.bcrypt.Bcrypt
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
        const val GOOGLE_AUTH_URL = "oauth2.googleapis.com/token"
        const val GOOGLE_USER_INFO_URL = "www.googleapis.com/oauth2/v3/userinfo"
        const val GOOGLE_GRANT_TYPE = "authorization_code"
        const val FACEBOOK_AUTH_URL = "graph.facebook.com/v12.0/oauth/access_token"
        const val FACEBOOK_USER_INFO_URL = "graph.facebook.com/me"
        const val JWT_ISSUER = "alpha-kibela"
        const val JWT_ACCESS_TOKEN_AUDIENCE = "alpha-kibela-access-token"
        const val JWT_REFRESH_TOKEN_AUDIENCE = "alpha-kibela-refresh-token"
        const val JWT_ACCESS_TOKEN_EXPIRATION_TIME: Long = 60 * 60 * 1
        const val JWT_REFRESH_TOKEN_EXPIRATION_TIME: Long = 60 * 60 * 24 * 30
    }

    suspend fun authenticateStandard(authRequestDto: StandardAuthRequestDto): UniResult<AuthResponseDto> {
        try {
            val userObject = userService.findByUsername(authRequestDto.username).then {
                logger.error { it.error.message }
                return it
            }
            val isPasswordValid =
                Bcrypt.verify(authRequestDto.password, userObject.password.toString().encodeToByteArray())
            if (!isPasswordValid) {
                logger.error { "Invalid password" }
                return AppError(CodeFactory.USER.UNAUTHORIZED, "Unauthorized").wrapError()
            }

            val (accessToken, refreshToken) = generateTokenPair(userObject)

            storeUserDataRedis(userObject, accessToken, refreshToken)

            val authResponse = AuthResponseDto(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresIn = JWT_ACCESS_TOKEN_EXPIRATION_TIME.toInt()
            )

            return authResponse.wrapResult()
        } catch (e: Exception) {
            logger.error { e.message }
            return AppError(CodeFactory.USER.INTERNAL_SERVER_ERROR, "Unexpected error occurred").wrapError()
        }
    }

    suspend fun authenticateGoogle(authRequestDto: OAuthRequestDto): UniResult<AuthResponseDto> {
        try {
            val googleAuthResponse = handleAuthenticateGoogle(authRequestDto).then {
                logger.error { it.error.message }
                return it
            }
            val googleUserInfoResponse = handleUserInfoGoogle(googleAuthResponse.accessToken).then {
                logger.error { it.error.message }
                return it
            }
            val userObject =
                userService.findOAuthUserAndCreateIfNotExist(ServiceType.GOOGLE, googleUserInfoResponse.toUserObject())
                    .then {
                        logger.error { it.error.message }
                        return it
                    }
            val (accessToken, refreshToken) = generateTokenPair(userObject)

            storeGoogleUserDataRedis(userObject, accessToken, refreshToken, googleAuthResponse)

            val authResponse = AuthResponseDto(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresIn = JWT_ACCESS_TOKEN_EXPIRATION_TIME.toInt()
            )

            return authResponse.wrapResult()
        } catch (e: Exception) {
            logger.error { e.message }
            return AppError(CodeFactory.USER.INTERNAL_SERVER_ERROR, "Unexpected error occurred").wrapError()
        }
    }

    suspend fun authenticateFacebook(authRequestDto: OAuthRequestDto): UniResult<AuthResponseDto> {
        try {
            val facebookAuthResponse = handleAuthenticateFacebook(authRequestDto).then {
                logger.error { it.error.message }
                return it
            }
            val facebookUserInfoResponse = handleUserInfoFacebook(facebookAuthResponse.accessToken).then {
                logger.error { it.error.message }
                return it
            }
            val userObject = userService.findOAuthUserAndCreateIfNotExist(
                ServiceType.FACEBOOK,
                facebookUserInfoResponse.toUserObject()
            ).then {
                logger.error { it.error.message }
                return it
            }
            val (accessToken, refreshToken) = generateTokenPair(userObject)

            storeFacebookUserDataRedis(userObject, accessToken, refreshToken, facebookAuthResponse)

            val authResponse = AuthResponseDto(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresIn = JWT_ACCESS_TOKEN_EXPIRATION_TIME.toInt()
            )

            return authResponse.wrapResult()
        } catch (e: Exception) {
            logger.error { e.message }
            return AppError(CodeFactory.USER.INTERNAL_SERVER_ERROR, "Unexpected error occurred").wrapError()
        }
    }

    private suspend fun handleAuthenticateGoogle(authRequest: OAuthRequestDto): UniResult<GoogleAuthResponseDto> {
        val clientId = getEnvOrError("GOOGLE_CLIENT_ID")
        val clientSecret = getEnvOrError("GOOGLE_CLIENT_SECRET")
        val redirectUri = getEnvOrError("REDIRECT_URI")
        val googleAuthRequest = GoogleAuthRequestDto(
            code = authRequest.code,
            clientId = clientId,
            clientSecret = clientSecret,
            redirectUri = redirectUri,
            grantType = GOOGLE_GRANT_TYPE
        )

        return httpClient.post(GOOGLE_AUTH_URL, googleAuthRequest)
            .deserializeWithStatus<GoogleAuthResponseDto>(HttpStatusCode.OK) {
                logger.error { "Failed to deserialize google auth response" }
                return AppError(CodeFactory.USER.UNAUTHORIZED, "Unauthorized").wrapError()
            }.wrapResult()
    }

    private suspend fun handleAuthenticateFacebook(authRequestDto: OAuthRequestDto): UniResult<FacebookAuthResponseDto> {
        val clientId = getEnvOrError("FACEBOOK_CLIENT_ID")
        val clientSecret = getEnvOrError("FACEBOOK_CLIENT_SECRET")
        val redirectUri = getEnvOrError("REDIRECT_URI")
        val facebookAuthRequest = FacebookAuthRequestDto(
            code = authRequestDto.code,
            clientId = clientId,
            clientSecret = clientSecret,
            redirectUri = redirectUri
        )

        return httpClient.post(FACEBOOK_AUTH_URL, facebookAuthRequest)
            .deserializeWithStatus<FacebookAuthResponseDto>(HttpStatusCode.OK) {
                logger.error { "Failed to deserialize facebook auth response" }
                return AppError(CodeFactory.USER.UNAUTHORIZED, "Unauthorized").wrapError()
            }.wrapResult()
    }

    private suspend fun handleUserInfoGoogle(accessToken: String): UniResult<GoogleUserInfoResponseDto> {
        val googleUserInfoResponse = httpClient.get(GOOGLE_USER_INFO_URL) {
            authorization = "Bearer $accessToken"
        }

        return googleUserInfoResponse.deserializeWithStatus<GoogleUserInfoResponseDto>(HttpStatusCode.OK) {
            logger.error { "Failed to deserialize google user info response" }
            return AppError(CodeFactory.USER.INTERNAL_SERVER_ERROR, "Failed to get google user info").wrapError()
        }.wrapResult()
    }

    private suspend fun handleUserInfoFacebook(accessToken: String): UniResult<FacebookUserInfoResponseDto> {
        val facebookUserInfoResponse = httpClient.get(FACEBOOK_USER_INFO_URL) {
            url {
                authorization = "Bearer $accessToken"
                parameters.append("fields", "id,name,picture,email")
            }
        }

        return facebookUserInfoResponse.deserializeWithStatus<FacebookUserInfoResponseDto>(HttpStatusCode.OK) {
            logger.error { "Failed to deserialize facebook user info response" }
            return AppError(CodeFactory.USER.INTERNAL_SERVER_ERROR, "Failed to get facebook user info").wrapError()
        }.wrapResult()
    }

    private fun generateTokenPair(userObject: UserObject): Pair<String, String> {
        val secret = getEnvOrError("JWT_SECRET")
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

    private suspend fun storeGoogleUserDataRedis(
        userObject: UserObject,
        accessToken: String,
        refreshToken: String,
        googleAuthResponse: GoogleAuthResponseDto
    ) {
        try {
            val id = userObject.id.toString()
            val keyPrefix = "user:$id"

            storeUserDataRedis(userObject, accessToken, refreshToken)

            redis.write("$keyPrefix:google:accessToken", googleAuthResponse.accessToken)
            redis.write("$keyPrefix:google:idToken", googleAuthResponse.idToken)
            redis.write("$keyPrefix:google:expiresIn", googleAuthResponse.expiresIn.toString())
        } catch (e: Exception) {
            logger.error { e.message }
            throw e
        }
    }

    private suspend fun storeFacebookUserDataRedis(
        userObject: UserObject,
        accessToken: String,
        refreshToken: String,
        facebookAuthResponse: FacebookAuthResponseDto
    ) {
        try {
            val id = userObject.id.toString()
            val keyPrefix = "user:$id"

            storeUserDataRedis(userObject, accessToken, refreshToken)

            redis.write("$keyPrefix:facebook:accessToken", facebookAuthResponse.accessToken)
            redis.write("$keyPrefix:facebook:expiresIn", facebookAuthResponse.expiresIn.toString())
        } catch (e: Exception) {
            logger.error { e.message }
            throw e
        }
    }

    private suspend fun storeUserDataRedis(userObject: UserObject, accessToken: String, refreshToken: String) {
        try {
            val id = userObject.id.toString()
            val keyPrefix = "user:$id"

            redis.write(keyPrefix, id)
            redis.write("$keyPrefix:sub", userObject.sub.toString())
            redis.write("$keyPrefix:serviceType", userObject.serviceType.toString())
            redis.write("$keyPrefix:accessToken", accessToken)
            redis.write("$keyPrefix:refreshToken", refreshToken)
        } catch (e: Exception) {
            logger.error { e.message }
            throw e
        }
    }
}