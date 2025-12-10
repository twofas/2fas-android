package com.twofasapp.data.notifications.mappper

import com.twofasapp.data.notifications.domain.Notification
import com.twofasapp.data.notifications.local.model.NotificationEntity
import com.twofasapp.data.notifications.remote.model.NotificationJson
import java.time.Instant

internal fun Notification.asEntity(periodicType: String? = null) = NotificationEntity(
    id = id,
    category = category.name,
    link = link,
    message = message,
    publishTime = createdAt,
    push = false,
    platform = "android",
    isRead = isRead,
    periodicType = periodicType,
    internalRoute = internalRoute,
)

internal fun NotificationEntity.asDomain() = Notification(
    id = id,
    category = Notification.Category.valueOf(category),
    link = link,
    message = message,
    createdAt = publishTime,
    isRead = isRead,
    internalRoute = internalRoute,
)

internal fun NotificationJson.asDomain() = Notification(
    id = id,
    category = Notification.Category.entries.find { category -> icon == category.icon } ?: Notification.Category.News,
    link = link,
    message = message,
    createdAt = Instant.parse(created_at).toEpochMilli(),
    isRead = false,
    internalRoute = null,
)