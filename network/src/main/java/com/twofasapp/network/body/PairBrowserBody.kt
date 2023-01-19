package com.twofasapp.network.body

import kotlinx.serialization.Serializable

@Serializable
class PairBrowserBody(
    val extension_id: String,
    val device_name: String,
    val device_public_key: String,
)