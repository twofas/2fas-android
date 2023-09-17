package com.twofasapp.feature.home.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twofasapp.data.notifications.NotificationsRepository
import com.twofasapp.data.notifications.domain.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class NotificationsViewModel(
    private val notificationsRepository: NotificationsRepository,
) : ViewModel() {

    val notificationsList = MutableStateFlow<List<Notification>>(emptyList())

    init {
        viewModelScope.launch {
            val notifications = notificationsRepository.getNotifications()
            notificationsRepository.readAllNotifications()

            notificationsList.update { notifications }
        }
    }

    fun onNotificationClick(notification: Notification) {
        notificationsList.update {
            it.map { item ->
                if (item.id == notification.id) {
                    item.copy(isRead = true)
                } else {
                    item
                }
            }
        }
    }
}