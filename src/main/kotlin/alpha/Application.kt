package alpha

import alpha.config.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDI()
    configureSecurity()
    configureDatabase()
    configureRouting()
    configureSerialization()
    configureExceptionInterceptor()
}
