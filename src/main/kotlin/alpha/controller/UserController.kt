package alpha.controller

import alpha.common.Role
import alpha.config.withRoles
import alpha.extension.respondError
import alpha.service.UserService
import com.github.michaelbull.result.mapBoth
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userController() {
    val userService by inject<UserService>()

    route("/users") {
        withRoles(Role.ADMIN) {
            get {
                userService.findAll().mapBoth(
                    success = { call.respond(it) },
                    failure = { call.respondError(it) }
                )
            }
        }
    }
}