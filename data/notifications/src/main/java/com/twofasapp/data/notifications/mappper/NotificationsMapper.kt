package com.twofasapp.data.notifications.mappper

import com.twofasapp.data.notifications.domain.Notification
import com.twofasapp.data.notifications.local.model.NotificationEntity
import com.twofasapp.data.notifications.remote.model.NotificationJson

internal fun Notification.asEntity() = NotificationEntity(
    id = id,
    category = category.name,
    link = link,
    message = message,
    publishTime = publishTime,
    push = push,
    platform = platform,
    isRead = isRead,
)

internal fun NotificationEntity.asDomain() = Notification(
    id = id,
    category = Notification.Category.valueOf(category),
    link = link,
    message = message,
    publishTime = publishTime,
    push = push,
    platform = platform,
    isRead = isRead,
)

internal fun NotificationJson.asDomain() = Notification(
    id = id,
    category = Notification.Category.values().find { category -> icon == category.icon } ?: Notification.Category.News,
    link = link,
    message = message,
    publishTime = java.time.OffsetDateTime.parse(published_at).toInstant().toEpochMilli(),
    push = push,
    platform = platform,
    isRead = false,
)