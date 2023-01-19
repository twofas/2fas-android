package com.twofasapp.notifications.domain

interface FetchNotificationsCase {
    suspend operator fun invoke()
}