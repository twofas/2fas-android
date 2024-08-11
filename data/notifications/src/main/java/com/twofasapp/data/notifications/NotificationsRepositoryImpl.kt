package com.twofasapp.data.notifications

import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.data.notifications.domain.Notification
import com.twofasapp.data.notifications.domain.PeriodicNotificationType
import com.twofasapp.data.notifications.local.NotificationsLocalSource
import com.twofasapp.data.notifications.mappper.asDomain
import com.twofasapp.data.notifications.remote.NotificationsRemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId

internal class NotificationsRepositoryImpl(
    private val dispatchers: Dispatchers,
    private val local: NotificationsLocalSource,
    private val remote: NotificationsRemoteSource,
) : NotificationsRepository {

    override suspend fun getNotifications(): List<Notification> {
        return withContext(dispatchers.io) {
            local.getNotifications().sortedByTime()
        }
    }

    override suspend fun fetchNotifications(sinceMillis: Long) {
        withContext(dispatchers.io) {
            val remoteData = remote.fetchNotifications(
                publishedAfter = Instant.ofEpochMilli(sinceMillis).atZone(ZoneId.systemDefault()).toOffsetDateTime()
            )
            local.saveNotifications(remoteData.map { it.asDomain() })
        }
    }

    override suspend fun readAllNotifications() {
        withContext(dispatchers.io) {
            local.readAllNotifications()
        }
    }

    override fun hasUnreadNotifications(): Flow<Boolean> {
        return local.observeNotifications()
            .map { it.sortedByTime() }
            .map { list -> list.any { it.isRead.not() } }
    }

    private fun List<Notification>.sortedByTime(): List<Notification> {
        return sortedByDescending { it.publishTime }
    }

    override suspend fun getPeriodicNotificationCounter(): Int {
        return withContext(dispatchers.io) {
            local.getPeriodicNotificationCounter()
        }
    }

    override suspend fun setPeriodicNotificationCounter(counter: Int) {
        withContext(dispatchers.io) {
            local.setPeriodicNotificationCounter(counter)
        }
    }

    override suspend fun getPeriodicNotificationTimestamp(): Long {
        return withContext(dispatchers.io) {
            local.getPeriodicNotificationTimestamp()
        }
    }

    override suspend fun setPeriodicNotificationTimestamp(timestamp: Long) {
        withContext(dispatchers.io) {
            local.setPeriodicNotificationTimestamp(timestamp)
        }
    }

    override suspend fun clearPeriodicNotifications() {
        withContext(dispatchers.io) {
            local.clearPeriodicNotifications()
        }
    }

    override suspend fun insertPeriodicNotification(type: PeriodicNotificationType, notification: Notification) {
        withContext(dispatchers.io) {
            local.insertPeriodicNotification(type, notification)
        }
    }
}