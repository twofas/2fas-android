package com.twofasapp.data.push.domain

sealed class Push(
    internal val handler: Handler
) {
    enum class Handler { InAppOnly, NotificationOnly, InAppOrNotification }

    data class BrowserExtRequest(
        val domain: String,
        val requestId: String,
        val extensionId: String,
    ) : Push(handler = Handler.NotificationOnly)
}
