package alpha.config

import alpha.controller.authController
import alpha.controller.userController
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        install(AuthenticationHeaderChallenge)
        authController()
        authenticate("accessTokenRequired") {
            userController()
        }
    }
}
