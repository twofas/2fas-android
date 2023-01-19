package com.twofasapp.prefs.model

import kotlinx.serialization.Serializable

@Serializable
data class LastPushesEntity(
    val pushes: List<PushEntity> = emptyList(),
) {
    @Serializable
    data class PushEntity(
        val timestamp: Long,
        val data: Map<String, String>,
        val notificationTitle: String?,
        val notificationBody: String?,
    )
}