package alpha.config

import alpha.error.AppError
import alpha.error.CodeFactory
import alpha.extension.respondError
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*

fun Application.configureExceptionInterceptor() {
    install(StatusPages) {
        exception<Exception> { call, cause ->
            call.respondError(AppError(CodeFactory.GENERAL.INTERNAL_SERVER_ERROR, cause.localizedMessage))
        }
    }
}