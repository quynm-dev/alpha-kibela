package alpha.controller

import alpha.common.ServiceType
import alpha.data.dto.request.OAuthRequestDto
import alpha.data.dto.request.StandardAuthRequestDto
import alpha.error.AppError
import alpha.error.CodeFactory
import alpha.extension.respondError
import alpha.service.AuthService
import com.github.michaelbull.result.mapBoth
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.authenticationController() {
    val authService by inject<AuthService>()

    route("/authenticate") {
        post("/standard") {
            val authRequestDto = call.receive<StandardAuthRequestDto>()
            authService.authenticateStandard(authRequestDto).mapBoth(
                success = { call.respond(it) },
                failure = { call.respondError(it) }
            )
        }

        post("/oauth") {
            val authRequestDto = call.receive<OAuthRequestDto>()
            val authResult = when (authRequestDto.provider) {
                ServiceType.GOOGLE -> authService.authenticateGoogle(authRequestDto)
                ServiceType.FACEBOOK -> authService.authenticateFacebook(authRequestDto)
                else -> return@post call.respondError(AppError(CodeFactory.GENERAL.BAD_REQUEST, "Invalid provider"))
            }
            authResult.mapBoth(
                success = { call.respond(it) },
                failure = { call.respondError(it) }
            )
        }

        authenticate("refreshTokenRequired") {
            // refresh token endpoint
        }
    }
}