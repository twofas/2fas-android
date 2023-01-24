package com.twofasapp.data.browserext.remote.model

import kotlinx.serialization.Serializable

@Serializable
internal data class RegisterDeviceJson(
    val id: String,
    val name: String,
    val platform: String,
    val created_at: String,
    val updated_at: String,
)