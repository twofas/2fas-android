package com.twofasapp.data.push.internal

import com.google.firebase.messaging.RemoteMessage

internal object PushFactory {

    fun createPush(remoteMessage: RemoteMessage): com.twofasapp.data.push.domain.Push? {
        return when (remoteMessage.data["type"]?.lowercase()) {
            "browser_extension_request" -> createBrowserExtension(remoteMessage)
            else -> null
        }
    }

    private fun createBrowserExtension(remoteMessage: RemoteMessage): com.twofasapp.data.push.domain.Push.BrowserExtRequest? {
        return try {
            com.twofasapp.data.push.domain.Push.BrowserExtRequest(
                domain = remoteMessage.data["domain"]!!,
                requestId = remoteMessage.data["request_id"]!!,
                extensionId = remoteMessage.data["extension_id"]!!,
            )
        } catch (e: Exception) {
            null
        }
    }
}