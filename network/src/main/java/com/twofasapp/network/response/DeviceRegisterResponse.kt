package com.twofasapp.network.response

import kotlinx.serialization.Serializable

@Serializable
class DeviceRegisterResponse(
    val id: String,
    val name: String,
    val platform: String,
    val created_at: String,
    val updated_at: String,
)