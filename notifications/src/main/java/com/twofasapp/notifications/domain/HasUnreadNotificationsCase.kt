package com.twofasapp.notifications.domain

import kotlinx.coroutines.flow.Flow

interface HasUnreadNotificationsCase {
    operator fun invoke(): Flow<Boolean>
}