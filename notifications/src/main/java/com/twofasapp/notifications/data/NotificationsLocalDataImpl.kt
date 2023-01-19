package com.twofasapp.notifications.data

import com.twofasapp.notifications.domain.converter.toDomain
import com.twofasapp.notifications.domain.converter.toEntity
import com.twofasapp.notifications.domain.model.Notification
import com.twofasapp.persistence.dao.NotificationDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class NotificationsLocalDataImpl(
    private val notificationDao: NotificationDao,
) : NotificationsLocalData {

    override suspend fun getNotifications(): List<Notification> {
        return notificationDao.select().map { it.toDomain() }
    }

    override fun observeNotifications(): Flow<List<Notification>> {
        return notificationDao.observe().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun saveNotifications(notifications: List<Notification>) {
        notificationDao.upsert(notifications.map { it.toEntity() })
    }

    override suspend fun deleteNotifications(ids: List<String>) {
        notificationDao.delete(ids)
    }

    override suspend fun readAllNotifications() {
        notificationDao.update(
            *notificationDao.select().map { it.copy(isRead = true) }.toTypedArray()
        )
    }
}