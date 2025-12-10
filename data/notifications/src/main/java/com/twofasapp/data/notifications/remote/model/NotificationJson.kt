package com.twofasapp.data.notifications.remote.model

import kotlinx.serialization.Serializable

@Serializable
internal class NotificationJson(
    val id: String,
    val icon: String,
    val link: String,
    val message: String,
    val created_at: String,
)