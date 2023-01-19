package com.twofasapp.network.response

import kotlinx.serialization.Serializable

@Serializable
class PairBrowserResponse(
    val extension_id: String,
    val extension_name: String,
    val extension_public_key: String,
)