package com.twofasapp.data.push.internal

import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

internal object PushLogger {

    fun logMessage(remoteMessage: RemoteMessage) {
        try {
            Timber.i("Data: ${remoteMessage.data}, notification.title=${remoteMessage.notification?.title}, notification.body=${remoteMessage.notification?.body}")
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun logToken(token: String) {
        Timber.i("onNewToken")
        Timber.i(token)

    }
}