package com.twofasapp.notifications.domain.repository

import com.twofasapp.notifications.data.NotificationsLocalData
import com.twofasapp.notifications.data.NotificationsRemoteData
import com.twofasapp.notifications.domain.model.Notification
import com.twofasapp.notifications.domain.repository.NotificationsRepository.Companion.publishedAfterDays
import com.twofasapp.prefs.model.CacheEntry
import com.twofasapp.prefs.usecase.CacheValidityPreference
import com.twofasapp.time.domain.TimeProvider
import kotlinx.coroutines.flow.Flow

internal class NotificationsRepositoryImpl(
    private val localData: NotificationsLocalData,
    private val remoteData: NotificationsRemoteData,
    private val cacheValidity: CacheValidityPreference,
    private val timeProvider: TimeProvider,
) : NotificationsRepository {

    override fun observeNotifications(): Flow<List<Notification>> {
        return localData.observeNotifications()
    }

    override suspend fun getNotifications(): List<Notification> {
        return localData.getNotifications()
    }

    override suspend fun fetchNotifications() {
        cacheValidity.runWithCacheValidation(CacheEntry.FetchNotifications) {

            val remoteData = remoteData.fetchNotifications(timeProvider.currentDateTimeUtc().minusDays(publishedAfterDays))
            localData.saveNotifications(remoteData)
        }
    }

    override suspend fun readAllNotifications() {
        localData.readAllNotifications()
    }
}