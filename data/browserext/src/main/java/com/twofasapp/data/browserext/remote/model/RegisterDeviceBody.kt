package com.twofasapp.data.browserext.remote.model

import kotlinx.serialization.Serializable

@Serializable
internal data class RegisterDeviceBody(
    val name: String,
    val fcm_token: String,
    val platform: String,
)