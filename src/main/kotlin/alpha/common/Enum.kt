package alpha.common

enum class Role(val number: Byte) {
    ADMIN(0),
    USER(1);

    companion object {
        fun from(number: Byte): Role {
            return entries.first { it.number == number }
        }
    }
}

enum class Status(val number: Byte) {
    ACTIVE(0),
    SUSPENDED(1),
    DELETED(2);

    companion object {
        fun from(number: Byte): Status {
            return Status.entries.first { it.number == number }
        }
    }
}

enum class ServiceType(val number: Byte) {
    STANDARD(0),
    GOOGLE(1),
    FACEBOOK(2);

    companion object {
        fun from(number: Byte): ServiceType {
            return ServiceType.entries.first { it.number == number }
        }
    }
}