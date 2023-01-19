package com.twofasapp.developer.domain.model

data class LastPush(
    val timestamp: Long,
    val data: Map<String, String>,
    val notificationTitle: String?,
    val notificationBody: String?,
)