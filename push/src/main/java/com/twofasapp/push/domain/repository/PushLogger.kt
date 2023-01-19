package com.twofasapp.push.domain.repository

import com.google.firebase.messaging.RemoteMessage
import com.twofasapp.environment.AppConfig
import com.twofasapp.prefs.model.LastPushesEntity
import com.twofasapp.prefs.usecase.LastPushesPreference
import com.twofasapp.time.domain.TimeProvider
import timber.log.Timber

class PushLogger(
    private val appConfig: AppConfig,
    private val timeProvider: TimeProvider,
    private val lastPushesPreference: LastPushesPreference,
) {

    fun logMessage(remoteMessage: RemoteMessage) {
        if (appConfig.isDebug) {
            try {
                Timber.i("Data: ${remoteMessage.data}, notification.title=${remoteMessage.notification?.title}, notification.body=${remoteMessage.notification?.body}")
                lastPushesPreference.put {
                    it.copy(
                        pushes = it.pushes.plus(
                            LastPushesEntity.PushEntity(
                                timestamp = timeProvider.systemCurrentTime(),
                                data = remoteMessage.data,
                                notificationTitle = remoteMessage.notification?.title,
                                notificationBody = remoteMessage.notification?.body,
                            )
                        )
                            .sortedByDescending { push -> push.timestamp }
                            .take(20)
                    )
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun logToken(token: String) {
        if (appConfig.isDebug) {
            Timber.i("onNewToken")
            Timber.i(token)
        }
    }
}