package com.twofasapp.data.notifications.local

import com.twofasapp.data.notifications.domain.Notification
import com.twofasapp.data.notifications.domain.PeriodicNotificationType
import com.twofasapp.data.notifications.mappper.asDomain
import com.twofasapp.data.notifications.mappper.asEntity
import com.twofasapp.storage.PlainPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class NotificationsLocalSource(
    private val notificationsDao: NotificationsDao,
    private val preferences: PlainPreferences,
) {

    companion object {
        private const val KeyPeriodicNotificationCounter = "periodicNotificationCounter"
        private const val KeyPeriodicNotificationTimestamp = "periodicNotificationTimestamp" // Time when last notification was triggered
    }

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

    suspend fun getPeriodicNotificationCounter(): Int {
        return preferences.getInt(KeyPeriodicNotificationCounter) ?: -1
    }

    suspend fun setPeriodicNotificationCounter(counter: Int) {
        preferences.putInt(KeyPeriodicNotificationCounter, counter)
    }

    suspend fun getPeriodicNotificationTimestamp(): Long {
        return preferences.getLong(KeyPeriodicNotificationTimestamp) ?: 0
    }

    suspend fun setPeriodicNotificationTimestamp(timestamp: Long) {
        preferences.putLong(KeyPeriodicNotificationTimestamp, timestamp)
    }

    suspend fun clearPeriodicNotifications() {
        notificationsDao.deleteAllPeriodic()
    }

    suspend fun insertPeriodicNotification(periodicType: PeriodicNotificationType, notification: Notification) {
        notificationsDao.insert(notification.asEntity(periodicType.name))
    }
}
