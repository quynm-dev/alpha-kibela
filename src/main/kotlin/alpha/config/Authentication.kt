package alpha.config

import alpha.error.AppError
import alpha.error.CodeFactory
import alpha.extension.respondError
import alpha.service.AuthService
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*

fun Application.configureAuthentication() {
    install(Authentication) {
        val secret = System.getenv("JWT_SECRET")
            ?: throw IllegalStateException("Missing JWT_SECRET environment variable")
        fun createTemplate() = JWT.require(Algorithm.HMAC256(secret)).withIssuer(AuthService.JWT_ISSUER)

        jwt("accessTokenRequired") {
            realm = "alpha-kibela access token"
            verifier(createTemplate().withAudience(AuthService.JWT_ACCESS_TOKEN_AUDIENCE).build())

            validate { credential ->
                if (credential.isExpired()) {
                    return@validate null
                }

                JWTPrincipal(credential.payload)
            }
        }

        jwt("refreshTokenRequired") {
            realm = "alpha-kibela refresh token"
            verifier(createTemplate().withAudience(AuthService.JWT_REFRESH_TOKEN_AUDIENCE).build())

            validate { credential ->
                if (credential.isExpired()) {
                    return@validate null
                }

                JWTPrincipal(credential.payload)
            }
        }
    }
}

val AuthenticationHeaderChallenge = createRouteScopedPlugin("AuthenticationHeaderPlugin") {
    on(AuthenticationChecked) {
        if (it.authentication.allErrors.isNotEmpty()) {
            it.respondError(AppError(CodeFactory.GENERAL.UNAUTHORIZED, "Unauthorized"))
        }
    }
}

private fun JWTCredential.isExpired(): Boolean {
    return expiresAt?.before(Date()) ?: true
}
