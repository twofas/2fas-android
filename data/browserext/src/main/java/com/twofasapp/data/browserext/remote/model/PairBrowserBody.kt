package com.twofasapp.data.browserext.remote.model

import kotlinx.serialization.Serializable

@Serializable
internal data class PairBrowserBody(
    val extension_id: String,
    val device_name: String,
    val device_public_key: String,
)