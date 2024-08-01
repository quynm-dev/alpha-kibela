package alpha.controller

import alpha.service.UserService
import com.github.michaelbull.result.mapBoth
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userController() {
    val userService by inject<UserService>()

    route("/users") {
        get("/") {
            userService.getAll().mapBoth(
                success = { call.respond(it) },
                failure = { err -> call.respond(err.code.status, err.toResponse()) }
            )
        }
    }
}