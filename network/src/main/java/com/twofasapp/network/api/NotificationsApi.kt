package com.twofasapp.network.api

import com.twofasapp.network.response.NotificationResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class NotificationsApi(
    private val client: HttpClient,
) {

    companion object {
        private const val baseUrl = "https://api2.2fas.com"
    }

    suspend fun fetchNotifications(publishedAfter: OffsetDateTime): List<NotificationResponse> =
        client.get("$baseUrl/mobile/notifications") {
            parameter("platform", "android")
            parameter("published_after", publishedAfter.format(DateTimeFormatter.ISO_INSTANT))
        }.body()
}
