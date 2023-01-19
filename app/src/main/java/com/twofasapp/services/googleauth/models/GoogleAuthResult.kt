package com.twofasapp.services.googleauth.models

sealed class GoogleAuthResult {
    data class Success(
        val email: String = ""
    ) : GoogleAuthResult()

    data class Canceled(
        val reason: CancelReason = CancelReason.CANCELED
    ) : GoogleAuthResult()

    data class Failure(
        val reason: Throwable
    ) : GoogleAuthResult()

    enum class CancelReason {
        CANCELED,
        PERMISSION_NOT_GRANTED,
        NETWORK_ERROR,
    }
}