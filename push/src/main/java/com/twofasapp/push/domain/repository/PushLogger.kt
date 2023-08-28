package com.twofasapp.push.domain.repository

import com.google.firebase.messaging.RemoteMessage
import com.twofasapp.common.environment.AppBuild
import timber.log.Timber

class PushLogger(
    private val appBuild: AppBuild,
) {

    fun logMessage(remoteMessage: RemoteMessage) {
        if (appBuild.isDebuggable) {
            try {
                Timber.i("Data: ${remoteMessage.data}, notification.title=${remoteMessage.notification?.title}, notification.body=${remoteMessage.notification?.body}")
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun logToken(token: String) {
        if (appBuild.isDebuggable) {
            Timber.i("onNewToken")
            Timber.i(token)
        }
    }
}