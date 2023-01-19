package com.twofasapp.push.domain.repository

import com.google.firebase.messaging.RemoteMessage
import com.twofasapp.push.domain.model.Push

internal class PushFactory {

    fun createPush(remoteMessage: RemoteMessage): Push? {
        return when (remoteMessage.data["type"]?.lowercase()) {
            "browser_extension_request" -> createBrowserExtension(remoteMessage)
            else -> null
        }
    }

    private fun createBrowserExtension(remoteMessage: RemoteMessage): Push.BrowserExtensionRequest? {
        return try {
            Push.BrowserExtensionRequest(
                domain = remoteMessage.data["domain"]!!,
                requestId = remoteMessage.data["request_id"]!!,
                extensionId = remoteMessage.data["extension_id"]!!,
            )
        } catch (e: Exception) {
            null
        }
    }
}