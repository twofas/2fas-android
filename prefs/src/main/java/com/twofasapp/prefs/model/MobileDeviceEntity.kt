package com.twofasapp.prefs.model

import kotlinx.serialization.Serializable

@Serializable
data class MobileDeviceEntity(
    val id: String,
    val name: String,
    val fcmToken: String,
    val platform: String,
    val publicKey: String,
)
