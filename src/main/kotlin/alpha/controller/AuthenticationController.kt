package alpha.controller

import alpha.data.dto.request.OAuthRequestDto
import alpha.data.dto.request.StandardAuthRequestDto
import alpha.extension.respondError
import alpha.service.AuthService
import com.github.michaelbull.result.mapBoth
import io.ktor.http.*
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
                success = {
                    call.response.cookies.append("accessToken", it.accessToken)
                    call.response.cookies.append("refreshToken", it.refreshToken)
                    call.response.cookies.append("expiresIn", it.expiresIn.toString())
                    call.respond(HttpStatusCode.OK)
                },
                failure = { call.respondError(it) }
            )
        }

        post("/google") {
            val authRequestDto = call.receive<OAuthRequestDto>()
            authService.authenticateGoogle(authRequestDto).mapBoth(
                success = {
                    call.response.cookies.append("accessToken", it.accessToken)
                    call.response.cookies.append("refreshToken", it.refreshToken)
                    call.response.cookies.append("expiresIn", it.expiresIn.toString())
                    call.respond(HttpStatusCode.OK)
                },
                failure = { call.respondError(it) }
            )
        }

        post("/facebook") {
            val authRequestDto = call.receive<OAuthRequestDto>()
            authService.authenticateFacebook(authRequestDto).mapBoth(
                success = {
                    call.response.cookies.append("accessToken", it.accessToken)
                    call.response.cookies.append("refreshToken", it.refreshToken)
                    call.response.cookies.append("expiresIn", it.expiresIn.toString())
                    call.respond(HttpStatusCode.OK)
                },
                failure = { call.respondError(it) }
            )
        }

        authenticate("refreshTokenRequired") {
            // refresh token endpoint
        }
    }
}