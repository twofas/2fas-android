package com.twofasapp.notifications.data

import com.twofasapp.notifications.domain.model.Notification
import kotlinx.coroutines.flow.Flow

internal interface NotificationsLocalData {
    suspend fun getNotifications(): List<Notification>
    fun observeNotifications(): Flow<List<Notification>>
    suspend fun saveNotifications(notifications: List<Notification>)
    suspend fun deleteNotifications(ids: List<String>)
    suspend fun readAllNotifications()
}