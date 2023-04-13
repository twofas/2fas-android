package com.twofasapp.data.notifications.local

import com.twofasapp.data.notifications.domain.Notification
import com.twofasapp.data.notifications.mappper.asDomain
import com.twofasapp.data.notifications.mappper.asEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class NotificationsLocalSource(
    private val notificationsDao: NotificationsDao,
) {

    suspend fun getNotifications(): List<Notification> {
        return notificationsDao.select().map { it.asDomain() }
    }

    fun observeNotifications(): Flow<List<Notification>> {
        return notificationsDao.observe().map { list ->
            list.map { it.asDomain() }
        }
    }

    suspend fun saveNotifications(notifications: List<Notification>) {
        notificationsDao.upsert(notifications.map { it.asEntity() })
    }

    suspend fun readAllNotifications() {
        notificationsDao.update(
            *notificationsDao.select().map { it.copy(isRead = true) }.toTypedArray()
        )
    }
}
