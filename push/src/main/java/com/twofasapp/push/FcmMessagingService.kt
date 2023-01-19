package com.twofasapp.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.twofasapp.push.domain.ObserveNotificationPushesCase
import com.twofasapp.push.domain.PushDispatchCase
import com.twofasapp.push.domain.ShowBrowserExtensionRequestNotificationCase
import com.twofasapp.push.domain.model.Push
import com.twofasapp.push.domain.repository.PushFactory
import com.twofasapp.push.domain.repository.PushLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FcmMessagingService : FirebaseMessagingService() {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val pushFactory: PushFactory = PushFactory()
    private val pushLogger: PushLogger by inject()
    private val pushDispatchCase: PushDispatchCase by inject()

    private val observeNotificationPushesCase: ObserveNotificationPushesCase by inject()
    private val showBrowserExtensionRequest: ShowBrowserExtensionRequestNotificationCase by inject()

    override fun onCreate() {
        super.onCreate()
        scope.launch(Dispatchers.IO) {
            observeNotificationPushesCase().flowOn(Dispatchers.IO).collect {
                when (it) {
                    is Push.BrowserExtensionRequest -> showBrowserExtensionRequest(it)
                }
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        pushLogger.logMessage(remoteMessage)
        pushFactory.createPush(remoteMessage)?.let { pushDispatchCase(it) }
    }

    override fun onNewToken(token: String) {
        pushLogger.logToken(token)
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
