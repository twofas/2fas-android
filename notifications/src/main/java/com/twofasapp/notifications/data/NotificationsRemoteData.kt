package com.twofasapp.notifications.data

import com.twofasapp.notifications.domain.model.Notification
import java.time.LocalDateTime
import java.time.OffsetDateTime

internal interface NotificationsRemoteData {
    suspend fun fetchNotifications(publishedAfter: OffsetDateTime): List<Notification>
}
