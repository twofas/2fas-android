package com.twofasapp.data.notifications.local

import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.common.storage.intPref
import com.twofasapp.common.storage.longPref
import com.twofasapp.data.notifications.domain.Notification
import com.twofasapp.data.notifications.domain.PeriodicNotificationType
import com.twofasapp.data.notifications.mappper.asDomain
import com.twofasapp.data.notifications.mappper.asEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class NotificationsLocalSource(
    dataStoreOwner: DataStoreOwner,
    private val notificationsDao: NotificationsDao,
) : DataStoreOwner by dataStoreOwner {

    private val periodicNotificationCounter by intPref(default = -1, name = "periodicNotificationCounter")
    private val periodicNotificationTimestamp by longPref(default = 0, name = "periodicNotificationTimestamp")

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
            *notificationsDao.select().map { it.copy(isRead = true) }.toTypedArray(),
        )
    }

    suspend fun getPeriodicNotificationCounter(): Int {
        return periodicNotificationCounter.get()
    }

    suspend fun setPeriodicNotificationCounter(counter: Int) {
        periodicNotificationCounter.set(counter)
    }

    suspend fun getPeriodicNotificationTimestamp(): Long {
        return periodicNotificationTimestamp.get()
    }

    suspend fun setPeriodicNotificationTimestamp(timestamp: Long) {
        periodicNotificationTimestamp.set(timestamp)
    }

    suspend fun clearPeriodicNotifications() {
        notificationsDao.deleteAllPeriodic()
    }

    suspend fun insertPeriodicNotification(periodicType: PeriodicNotificationType, notification: Notification) {
        notificationsDao.insert(notification.asEntity(periodicType.name))
    }
}