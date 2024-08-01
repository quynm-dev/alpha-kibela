package alpha.config.httpClient

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import mu.KotlinLogging
import org.koin.core.annotation.Factory

val logger = KotlinLogging.logger {}

@Factory([HttpClient::class, IHttpClient::class])
class HttpClient: IHttpClient {
    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 3)
            exponentialDelay()
        }
        install(Logging) { level = LogLevel.INFO }
    }

    override suspend fun <T> submit(url: String, method: HttpMethod, body: T?, configurer: IHttpClient.ClientConfig<T>.() -> Unit): HttpResponse {
        val config = IHttpClient.ClientConfig(url, body)
        configurer(config)
        try {
            return httpClient.request(config.url) {
                this.method = method
                config.body?.let { setBody(it as Any) }
                config.contentType?.let { contentType(it) }
                headers { config.authorization?.let { append(HttpHeaders.Authorization, it) } }
            }
        } catch (e: ClientRequestException) {
            logger.error { e.message }
            throw e
        } catch (e: ServerResponseException) {
            logger.error { e.message }
            throw e
        }
    }
}