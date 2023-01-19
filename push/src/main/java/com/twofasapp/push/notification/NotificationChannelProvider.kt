package com.twofasapp.push.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build

class NotificationChannelProvider(private val notificationManager: NotificationManager) {

    fun getBrowserExtensionChannelId(): String {
        val channelId = "Browser Extension"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Browser Extension"
            val channelImportance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, channelImportance)
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }

        return channelId
    }

}