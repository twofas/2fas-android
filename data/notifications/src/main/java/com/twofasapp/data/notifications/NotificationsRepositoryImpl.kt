package com.twofasapp.data.notifications

import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.notifications.domain.Notification
import com.twofasapp.data.notifications.local.NotificationsLocalSource
import com.twofasapp.data.notifications.mappper.asDomain
import com.twofasapp.data.notifications.remote.NotificationsRemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Duration

internal class NotificationsRepositoryImpl(
    private val dispatchers: Dispatchers,
    private val local: NotificationsLocalSource,
    private val remote: NotificationsRemoteSource,
    private val timeProvider: TimeProvider,
) : NotificationsRepository {

    companion object {
        private const val publishedAfterDays = 90L
    }

    override suspend fun getNotifications(): List<Notification> {
        return withContext(dispatchers.io) {
            local.getNotifications().filterOutTooOldNotifications()
        }
    }

    override suspend fun fetchNotifications() {
        withContext(dispatchers.io) {
            val remoteData = remote.fetchNotifications(timeProvider.currentDateTimeUtc().minusDays(publishedAfterDays))
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
            .map { it.filterOutTooOldNotifications() }
            .map { list -> list.any { it.isRead.not() } }
    }

    private fun List<Notification>.filterOutTooOldNotifications(): List<Notification> {
        return filter { it.publishTime > timeProvider.systemCurrentTime() - Duration.ofDays(publishedAfterDays).toMillis() }
            .sortedWith(compareBy({ it.isRead }, { it.publishTime.unaryMinus() }))
    }
}