package alpha

import alpha.config.*
import alpha.config.database.configureDatabase
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDI()
    configureAuthentication()
    configureSecurity()
    configureDatabase()
    configureRouting()
    configureSerialization()
    configureExceptionInterceptor()
}
