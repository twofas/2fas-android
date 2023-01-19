package com.twofasapp.network.body

import kotlinx.serialization.Serializable

@Serializable
class DeviceRegisterBody(
    val name: String,
    val fcm_token: String,
    val platform: String,
)