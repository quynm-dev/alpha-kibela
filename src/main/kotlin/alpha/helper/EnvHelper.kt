package alpha.helper

fun getEnv(key: String) = System.getenv(key)

fun getEnvOrDefaultInt(key: String, default: Int) = System.getenv(key)?.toIntOrNull() ?: default

fun getEnvOrError(key: String) = System.getenv(key) ?: throw IllegalStateException("Missing $key environment variable")