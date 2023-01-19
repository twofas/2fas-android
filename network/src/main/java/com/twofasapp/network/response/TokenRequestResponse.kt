package com.twofasapp.network.response

import kotlinx.serialization.Serializable

@Serializable
data class TokenRequestResponse(
    val extension_id: String,
    val token_request_id: String,
    val domain: String,
    val status: String,
)