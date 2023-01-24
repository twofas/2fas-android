package com.twofasapp.data.browserext.local.model

import kotlinx.serialization.Serializable

@Serializable
internal data class MobileDeviceEntity(
    val id: String,
    val name: String,
    val fcmToken: String,
    val platform: String,
    val publicKey: String,
)
