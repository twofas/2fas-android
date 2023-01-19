package com.twofasapp.notifications.domain

import com.twofasapp.notifications.domain.repository.NotificationsRepository

internal class ReadAllNotificationsCase(
    private val notificationsRepository: NotificationsRepository,
) {

    suspend operator fun invoke() {
        return notificationsRepository.readAllNotifications()
    }
}