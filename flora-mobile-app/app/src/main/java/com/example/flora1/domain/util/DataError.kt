package com.example.flora1.domain.util

sealed interface DataError : Error {
    enum class Network : DataError {
        REQUEST_TIMEOUT,
        UNAUTHORIZED,
        CONFLICT,
        BAD_REQUEST,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        PAYLOAD_TOO_LARGE,
        SERVER_ERROR,
        SERIALIZATION,
        SOCKET_ERROR,
        UNKNOWN
    }

    enum class Local : DataError {
        DISK_FULL
    }
}
