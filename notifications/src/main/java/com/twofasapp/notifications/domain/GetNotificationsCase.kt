package com.twofasapp.notifications.domain

import com.twofasapp.notifications.domain.model.Notification
import com.twofasapp.notifications.domain.repository.NotificationsRepository
import com.twofasapp.notifications.domain.repository.NotificationsRepository.Companion.publishedAfterDays
import com.twofasapp.time.domain.TimeProvider
import java.time.Duration

internal class GetNotificationsCase(
    private val notificationsRepository: NotificationsRepository,
    private val timeProvider: TimeProvider,
) {

    suspend operator fun invoke(): List<Notification> {
        return notificationsRepository.getNotifications()
            .filter { it.publishTime > timeProvider.systemCurrentTime() - Duration.ofDays(publishedAfterDays).toMillis() }
            .sortedWith(compareBy({ it.isRead }, { it.publishTime.unaryMinus() }))
    }
}