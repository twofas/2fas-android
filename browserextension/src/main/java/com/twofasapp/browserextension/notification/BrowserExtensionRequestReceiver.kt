package com.twofasapp.browserextension.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BrowserExtensionRequestReceiver : BroadcastReceiver(), KoinComponent {

    private val notificationManager: NotificationManager by inject()
    private val json: Json by inject()

    companion object {
        const val ACTION = "BrowserExtensionRequest"

        fun createIntent(context: Context, payload: BrowserExtensionRequestPayload) =
            Intent(context, BrowserExtensionRequestReceiver::class.java).apply {
                action = ACTION

                putExtra(
                    BrowserExtensionRequestPayload.Key, payload
                )
            }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION) {
            val payload = intent.getParcelableExtra<BrowserExtensionRequestPayload>(BrowserExtensionRequestPayload.Key)!!

            if (payload.serviceId < 0 && payload.action == BrowserExtensionRequestPayload.Action.Approve) return

            val request = OneTimeWorkRequestBuilder<BrowserExtensionRequestWorker>()
                .setInputData(
                    Data.Builder().apply {
                        putString(BrowserExtensionRequestPayload.Key, json.encodeToString(payload))
                    }.build()
                )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork("BrowserExtensionRequestWorker", ExistingWorkPolicy.APPEND_OR_REPLACE, request)

            notificationManager.cancel(payload.notificationId)
        }
    }
}