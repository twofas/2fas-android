package com.twofasapp.notifications.data

import com.twofasapp.network.api.NotificationsApi
import com.twofasapp.notifications.domain.converter.toDomain
import com.twofasapp.notifications.domain.model.Notification
import java.time.OffsetDateTime

internal class NotificationsRemoteDataImpl(
    private val notificationsApi: NotificationsApi
) : NotificationsRemoteData {

    override suspend fun fetchNotifications(publishedAfter: OffsetDateTime): List<Notification> {
        return notificationsApi.fetchNotifications(publishedAfter)
            .filter { it.published_at.isNotBlank() }
            .map { it.toDomain() }
    }
}
