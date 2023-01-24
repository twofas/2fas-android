package com.twofasapp.data.browserext.domain

data class MobileDevice(
    val id: String,
    val name: String,
    val fcmToken: String,
    val platform: String,
    val publicKey: String,
)
