package alpha.controller

import alpha.data.dto.request.OAuthRequestDto
import alpha.extension.respondError
import alpha.service.AuthService
import com.github.michaelbull.result.mapBoth
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.authController() {
    val authService by inject<AuthService>()

    route("/authenticate") {
        post("/standard") {

        }

        post("/google") {
            val authRequestDto = call.receive<OAuthRequestDto>()
            authService.authenticateGoogle(authRequestDto).mapBoth(
                success = { call.respond(it) },
                failure = { call.respondError(it) }
            )
        }

        post("/facebook") {

        }

        authenticate("refreshTokenRequired") {
            // refresh token endpoint
        }
    }
}