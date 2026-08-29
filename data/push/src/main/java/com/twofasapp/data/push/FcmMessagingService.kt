package com.twofasapp.data.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.data.push.domain.Push
import com.twofasapp.data.push.internal.PushFactory
import com.twofasapp.data.push.internal.PushLogger
import com.twofasapp.data.push.notification.ShowBrowserExtRequestNotification
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

class FcmMessagingService : FirebaseMessagingService() {

    private val appBuild: AppBuild by inject()

    private val showBrowserExtensionRequest: ShowBrowserExtRequestNotification by inject()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (appBuild.debuggable) {
            PushLogger.logMessage(remoteMessage)
        }

        val push = PushFactory.createPush(remoteMessage) ?: return

        when (push.handler) {
            Push.Handler.InAppOnly -> Unit // Unsupported for now, we don't have in-app push yet

            Push.Handler.NotificationOnly -> {
                showNotification(push)
            }

            Push.Handler.InAppOrNotification -> {
                showNotification(push)
            }
        }
    }

    override fun onNewToken(token: String) {
        if (appBuild.debuggable) {
            PushLogger.logToken(token)
        }
    }

    private fun showNotification(push: Push) = runBlocking {
        when (push) {
            is Push.BrowserExtRequest -> showBrowserExtensionRequest(push)
        }
    }
}
