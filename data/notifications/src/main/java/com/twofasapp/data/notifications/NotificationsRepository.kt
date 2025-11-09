package com.twofasapp.data.notifications

import com.twofasapp.data.notifications.domain.Notification
import com.twofasapp.data.notifications.domain.PeriodicNotificationType
import kotlinx.coroutines.flow.Flow

interface NotificationsRepository {
    suspend fun getNotifications(): List<Notification>
    suspend fun fetchNotifications(appInstallTimeMillis: Long, noCompanionAppFromTimeMillis: Long?)
    suspend fun readAllNotifications()
    fun hasUnreadNotifications(): Flow<Boolean>
    suspend fun getPeriodicNotificationCounter(): Int
    suspend fun setPeriodicNotificationCounter(counter: Int)
    suspend fun getPeriodicNotificationTimestamp(): Long
    suspend fun setPeriodicNotificationTimestamp(timestamp: Long)
    suspend fun clearPeriodicNotifications()
    suspend fun insertPeriodicNotification(type: PeriodicNotificationType, notification: Notification)
}