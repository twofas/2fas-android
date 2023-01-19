package com.twofasapp.security.domain.model

enum class PinTimeout(
    val timeoutMs: Int,
    val label: String,
) {
    Timeout3(3 * 60 * 1000, "3 minutes"),
    Timeout5(5 * 60 * 1000, "5 minutes"),
    Timeout10(10 * 60 * 1000, "10 minutes"),
}