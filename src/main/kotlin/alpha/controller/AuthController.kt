package alpha.controller

import alpha.data.dto.request.AuthRequest
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
        post {
            val authRequest = call.receive<AuthRequest>()
            authService.authenticate(authRequest).mapBoth(
                success = { call.respond(it) },
                failure = { call.respondError(it) }
            )
        }

        authenticate("refreshTokenRequired") {
            // refresh token endpoint
        }
    }
}