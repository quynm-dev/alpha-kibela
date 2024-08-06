package alpha.config

import alpha.controller.authenticationController
import alpha.controller.registerController
import alpha.controller.userController
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        install(AuthenticationHeaderChallenge)
        authenticationController()
        registerController()
        authenticate("accessTokenRequired") {
            userController()
        }
    }
}
