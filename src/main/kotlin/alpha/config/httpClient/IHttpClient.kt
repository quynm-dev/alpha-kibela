package alpha.config.httpClient

import io.ktor.client.statement.*
import io.ktor.http.*

interface IHttpClient {
    suspend fun get(url: String, configurer: ClientConfig<Unit>.() -> Unit = {}) =
        submit(url, HttpMethod.Get, null, configurer)

    suspend fun <T> post(url: String, body: T, configurer: ClientConfig<T>.() -> Unit = {}) =
        submit(url, HttpMethod.Post, body, configurer)

    suspend fun <T> submit(
        url: String,
        method: HttpMethod,
        body: T?,
        configurer: ClientConfig<T>.() -> Unit = {}
    ): HttpResponse

    class ClientConfig<T>(url: String, var body: T? = null) {
        var url = run {
            val builder = URLBuilder()
            builder.host = url
            builder.protocol = URLProtocol.HTTPS
            builder.buildString()
        }
        var authorization: String? = null
        var contentType: ContentType? = ContentType.Application.Json

        fun path(path: String) = url { appendPathSegments(path) }

        fun url(builder: URLBuilder.() -> Unit) {
            val urlBuilder = URLBuilder(url)
            builder(urlBuilder)
            url = urlBuilder.buildString()
        }
    }
}
