package com.twofasapp.data.browserext.remote.model

import kotlinx.serialization.Serializable

@Serializable
internal data class DenyLoginRequestBody(
    val status: String = "completed"
)