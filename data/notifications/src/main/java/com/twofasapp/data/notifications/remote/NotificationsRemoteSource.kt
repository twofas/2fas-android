package com.twofasapp.data.notifications.remote

import com.twofasapp.data.notifications.remote.model.NotificationJson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

internal class NotificationsRemoteSource(
    private val client: HttpClient,
) {

    suspend fun fetchNotifications(publishedAfter: OffsetDateTime): List<NotificationJson> {
        return client.get("/mobile/notifications") {
            parameter("platform", "android")
            parameter("published_after", publishedAfter.format(DateTimeFormatter.ISO_INSTANT))
        }.body()
    }
}
