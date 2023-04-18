package com.twofasapp.data.browserext.remote.model

import kotlinx.serialization.Serializable

@Serializable
internal data class BrowserJson(
    val id: String,
    val name: String,
    val paired_at: String,
)