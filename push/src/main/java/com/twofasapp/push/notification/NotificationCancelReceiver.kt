package com.twofasapp.push.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class NotificationCancelReceiver : BroadcastReceiver(), KoinComponent {

    private val notificationManager: NotificationManager by inject()

    companion object {
        const val KEY_NOTIFICATION_ID = "notificationId"
        const val ACTION = "NotificationCancelReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION) {
            try {
                val notificationId = intent.getIntExtra(KEY_NOTIFICATION_ID, 0)
                notificationManager.cancel(notificationId)
            } catch (_: Exception) {
            }
        }
    }
}

fun NotificationManager.cancelNotificationPendingIntent(context: Context, notificationId: Int): PendingIntent = PendingIntent.getBroadcast(
    context,
    notificationId,
    Intent(context, NotificationCancelReceiver::class.java).apply {
        action = NotificationCancelReceiver.ACTION

        putExtra(NotificationCancelReceiver.KEY_NOTIFICATION_ID, notificationId)
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
)