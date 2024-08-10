package alpha.controller

import alpha.data.dto.request.CreateUserRequestDto
import alpha.extension.respondError
import alpha.mapper.toObject
import alpha.service.UserService
import com.github.michaelbull.result.mapBoth
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.registerController() {
    val userService by inject<UserService>()

    route("/register") {
        post {
            val createUserRequestDto = call.receive<CreateUserRequestDto>()
            userService.register(createUserRequestDto.toObject()).mapBoth(
                success = { call.respond(HttpStatusCode.Created) },
                failure = { call.respondError(it) }
            )
        }
    }
}