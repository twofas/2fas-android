package com.twofasapp.data.notifications

import com.twofasapp.data.notifications.domain.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationsRepository {
    suspend fun getNotifications(): List<Notification>
    suspend fun fetchNotifications(sinceMillis: Long)
    suspend fun readAllNotifications()
    fun hasUnreadNotifications(): Flow<Boolean>
}