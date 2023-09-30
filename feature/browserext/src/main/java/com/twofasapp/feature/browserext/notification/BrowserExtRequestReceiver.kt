package com.twofasapp.feature.browserext.notification

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

class BrowserExtRequestReceiver : BroadcastReceiver(), KoinComponent {

    private val notificationManager: NotificationManager by inject()
    private val json: Json by inject()

    companion object {
        const val ACTION = "BrowserExtRequest"

        fun createIntent(context: Context, payload: BrowserExtRequestPayload) =
            Intent(context, BrowserExtRequestReceiver::class.java).apply {
                action = ACTION

                putExtra(
                    BrowserExtRequestPayload.Key, payload
                )
            }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION) {
            val payload = intent.getParcelableExtra<BrowserExtRequestPayload>(BrowserExtRequestPayload.Key)!!

            if (payload.serviceId < 0 && payload.action == BrowserExtRequestPayload.Action.Approve) return

            val request = OneTimeWorkRequestBuilder<BrowserExtRequestWorker>()
                .setInputData(
                    Data.Builder().apply {
                        putString(BrowserExtRequestPayload.Key, json.encodeToString(payload))
                    }.build()
                )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork("BrowserExtRequestWorker", ExistingWorkPolicy.APPEND_OR_REPLACE, request)

            notificationManager.cancel(payload.notificationId)
        }
    }
}