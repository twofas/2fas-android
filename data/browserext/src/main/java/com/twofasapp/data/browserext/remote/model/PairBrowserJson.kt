package com.twofasapp.data.browserext.remote.model

import kotlinx.serialization.Serializable

@Serializable
internal data class PairBrowserJson(
    val extension_id: String,
    val extension_name: String,
    val extension_public_key: String,
)