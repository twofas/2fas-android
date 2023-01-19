package com.twofasapp.network.response

import kotlinx.serialization.Serializable

@Serializable
class BrowserResponse(
    val id: String,
    val name: String,
    val paired_at: String,
)