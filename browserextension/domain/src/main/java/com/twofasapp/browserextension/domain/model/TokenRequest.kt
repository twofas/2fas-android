package com.twofasapp.browserextension.domain.model

data class TokenRequest(
    val domain: String,
    val requestId: String,
    val extensionId: String,
)