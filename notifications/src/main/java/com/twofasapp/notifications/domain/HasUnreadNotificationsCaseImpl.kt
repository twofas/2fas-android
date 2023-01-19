package com.twofasapp.notifications.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class HasUnreadNotificationsCaseImpl(
    private val observeNotificationsCase: ObserveNotificationsCase,
) : HasUnreadNotificationsCase {

    override operator fun invoke(): Flow<Boolean> {
        return observeNotificationsCase().map { list ->
            list.any { it.isRead.not() }
        }
    }
}