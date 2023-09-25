package com.twofasapp.data.session.domain

data class InvalidPinStatus(
    val attempts: Int = 0,
    val lastAttemptSinceBootMs: Long = 0,
    val shouldBlock: Boolean = false,
    val timeLeftMs: Long = 0,
    val timeLeftMin: Int = 0,
) {
    companion object {
        val Default = InvalidPinStatus()
    }
}
