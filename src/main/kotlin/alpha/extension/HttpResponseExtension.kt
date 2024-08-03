package alpha.extension

import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

suspend inline fun <reified T : Any> HttpResponse.deserializeWithStatus(
    statusCode: HttpStatusCode,
    shortCircuit: HttpResponse.(HttpStatusCode) -> Unit
): T {
    if (status == statusCode) {
        return Json.decodeFromString<T>(this.bodyAsText())
    }
    shortCircuit(this, statusCode)
    throw IllegalStateException("Illegal state")
}