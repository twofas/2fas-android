package com.twofasapp.notifications.domain

import com.twofasapp.notifications.domain.model.Notification
import com.twofasapp.notifications.domain.repository.NotificationsRepository
import com.twofasapp.notifications.domain.repository.NotificationsRepository.Companion.publishedAfterDays
import com.twofasapp.time.domain.TimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Duration

internal class ObserveNotificationsCase(
    private val notificationsRepository: NotificationsRepository,
    private val timeProvider: TimeProvider,
) {

    operator fun invoke(): Flow<List<Notification>> {
        return notificationsRepository.observeNotifications()
            .map { list ->
                list
                    .filter { it.publishTime > timeProvider.systemCurrentTime() - Duration.ofDays(publishedAfterDays).toMillis() }
                    .sortedWith(compareBy({ it.isRead }, { it.publishTime.unaryMinus() }))
            }
    }
}