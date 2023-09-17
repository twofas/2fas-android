package com.twofasapp.data.cloud.googleauth

sealed class SignInResult {
    data class Success(
        val email: String = ""
    ) : SignInResult()

    data class Canceled(
        val reason: CancelReason = CancelReason.Canceled
    ) : SignInResult()

    data class Failure(
        val reason: Throwable
    ) : SignInResult()

    enum class CancelReason {
        Canceled,
        PermissionNotGranted,
        NetworkError,
    }
}