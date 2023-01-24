package com.twofasapp.data.browserext.remote.model

import kotlinx.serialization.Serializable

@Serializable
internal data class ApproveLoginRequestBody(
    val extension_id: String,
    val token_request_id: String,
    val token: String,
)