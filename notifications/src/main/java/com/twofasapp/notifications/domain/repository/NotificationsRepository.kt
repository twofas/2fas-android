package com.twofasapp.notifications.domain.repository

import com.twofasapp.notifications.domain.model.Notification
import kotlinx.coroutines.flow.Flow

internal interface NotificationsRepository {

    companion object {
        const val publishedAfterDays = 90L
    }

    fun observeNotifications(): Flow<List<Notification>>
    suspend fun getNotifications(): List<Notification>
    suspend fun fetchNotifications()
    suspend fun readAllNotifications()
}