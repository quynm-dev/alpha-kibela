package alpha.extension

import io.ktor.server.application.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*
import mu.KotlinLogging

val logger = KotlinLogging.logger {}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Any> ApplicationCall.receiveParams(): T {
    val j = Json {
        ignoreUnknownKeys = true
    }
    val paramObj = buildJsonObject {
        parameters.forEach { key, values ->
            if (key.endsWith("[]")) {
                putJsonArray(key.dropLast(2)) { addAll(values) }
            } else if (values.isNotEmpty()) {
                put(key, values.first())
            }
        }
    }

    return try {
        j.decodeFromJsonElement(paramObj)
    } catch (e: MissingFieldException) {
        logger.error { e.message }
        throw e
    } catch (e: SerializationException) {
        logger.error { e.message }
        throw e
    }
}