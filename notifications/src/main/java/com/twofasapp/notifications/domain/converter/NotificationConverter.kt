package com.twofasapp.notifications.domain.converter

import com.twofasapp.network.response.NotificationResponse
import com.twofasapp.notifications.domain.model.Notification
import com.twofasapp.persistence.model.NotificationEntity
import java.time.OffsetDateTime

internal fun Notification.toEntity() = NotificationEntity(
    id = id,
    category = category.name,
    link = link,
    message = message,
    publishTime = publishTime,
    push = push,
    platform = platform,
    isRead = isRead,
)

internal fun NotificationEntity.toDomain() = Notification(
    id = id,
    category = Notification.Category.valueOf(category),
    link = link,
    message = message,
    publishTime = publishTime,
    push = push,
    platform = platform,
    isRead = isRead,
)

internal fun NotificationResponse.toDomain() = Notification(
    id = id,
    category = Notification.Category.values().find { category -> icon == category.icon } ?: Notification.Category.News,
    link = link,
    message = message,
    publishTime = OffsetDateTime.parse(published_at).toInstant().toEpochMilli(),
    push = push,
    platform = platform,
    isRead = false,
)