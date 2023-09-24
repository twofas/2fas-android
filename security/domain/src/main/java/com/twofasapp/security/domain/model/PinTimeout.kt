package com.twofasapp.security.domain.model

enum class PinTimeout(
    val timeoutMs: Int,
    val label: Int,
) {
    Timeout3(3 * 60 * 1000, com.twofasapp.locale.R.string.settings__3_minutes),
    Timeout5(5 * 60 * 1000, com.twofasapp.locale.R.string.settings__5_minutes),
    Timeout10(10 * 60 * 1000, com.twofasapp.locale.R.string.settings__10_minutes),
}