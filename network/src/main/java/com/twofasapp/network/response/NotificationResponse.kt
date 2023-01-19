package com.twofasapp.network.response

import kotlinx.serialization.Serializable

@Serializable
class NotificationResponse(
    val id: String,
    val icon: String,
    val link: String,
    val message: String,
    val published_at: String,
    val push: Boolean,
    val platform: String,
)