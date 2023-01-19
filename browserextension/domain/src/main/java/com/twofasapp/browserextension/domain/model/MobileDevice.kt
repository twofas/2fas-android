package com.twofasapp.browserextension.domain.model

data class MobileDevice(
    val id: String,
    val name: String,
    val fcmToken: String,
    val platform: String,
    val publicKey: String,
)
