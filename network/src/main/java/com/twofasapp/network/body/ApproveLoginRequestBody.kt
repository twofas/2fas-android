package com.twofasapp.network.body

import kotlinx.serialization.Serializable

@Serializable
class ApproveLoginRequestBody(
    val extension_id: String,
    val token_request_id: String,
    val token: String,
)