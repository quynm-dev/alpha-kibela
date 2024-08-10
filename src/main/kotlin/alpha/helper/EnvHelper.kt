package alpha.helper

fun getEnv(key: String): String? {
    return System.getenv(key)
}

fun getEnvOrError(key: String): String {
    return System.getenv(key) ?: throw IllegalStateException("Missing $key environment variable")
}

fun getEnvOrDefaultInt(key: String, default: Int): Int {
    return System.getenv(key)?.toIntOrNull() ?: default
}