package com.twofasapp.notifications.domain

import com.twofasapp.notifications.domain.repository.NotificationsRepository
import timber.log.Timber

internal class FetchNotificationsCaseImpl(
    private val notificationsRepository: NotificationsRepository,
) : FetchNotificationsCase {

    override suspend operator fun invoke() {
        return try {
            notificationsRepository.fetchNotifications()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}