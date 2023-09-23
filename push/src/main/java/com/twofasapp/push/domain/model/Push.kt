package com.twofasapp.push.domain.model

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
