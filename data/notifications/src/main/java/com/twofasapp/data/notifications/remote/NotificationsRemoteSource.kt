package com.twofasapp.data.notifications.remote

import com.twofasapp.data.notifications.remote.model.NotificationJson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Locale

internal class NotificationsRemoteSource(
    private val client: HttpClient,
) {

    suspend fun fetchNotifications(
        appInstallTime: Instant?,
        noCompanionAppFromTime: Instant?,
    ): List<NotificationJson> {
        return client.get("https://notify.2fas.com") {
            parameter("platform", "android")
            parameter("app", "auth")
            parameter("lang", Locale.getDefault().toString())

            appInstallTime?.let {
                parameter("published_after", it.asIsoTimestamp())
            }

            noCompanionAppFromTime?.let {
                parameter("no_companion_app_from", it.asIsoTimestamp())
            }
        }.body()
    }

    private fun Instant.asIsoTimestamp(): String {
        return DateTimeFormatter.ISO_INSTANT.format(this)
    }
}
