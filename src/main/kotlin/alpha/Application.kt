package alpha

import alpha.config.configureDI
import alpha.config.configureDatabase
import alpha.config.configureRouting
import alpha.config.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDI()
    configureDatabase()
    configureRouting()
    configureSerialization()
}
