package alpha.error

import io.ktor.http.*

data class Code(val domain: Domain, val status: HttpStatusCode)

class CodeFactory {
    companion object {
        val GENERAL = GeneralCode(Domain.GENERAL)
        val USER = UserCode()
    }

    open class GeneralCode(private val domain: Domain) {
        val BAD_REQUEST = buildCode(HttpStatusCode.BadRequest)
        val UNAUTHORIZED = buildCode(HttpStatusCode.Unauthorized)
        val FORBIDDEN = buildCode(HttpStatusCode.Forbidden)
        val NOT_FOUND = buildCode(HttpStatusCode.NotFound)
        val DB_ERROR = buildCode(HttpStatusCode.InternalServerError)
        val INTERNAL_SERVER_ERROR = buildCode(HttpStatusCode.InternalServerError)

        fun buildCode(status: HttpStatusCode): Code {
            return Code(domain, status)
        }
    }

    class UserCode : GeneralCode(Domain.USER) {
        val CONFLICT = buildCode(HttpStatusCode.Conflict)
    }
}