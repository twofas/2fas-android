package com.twofasapp.data.browserext.domain

data class TokenRequest(
    val domain: String,
    val requestId: String,
    val extensionId: String,
)