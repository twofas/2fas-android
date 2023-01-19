package com.twofasapp.security.domain.model

enum class PinDigits(
    val value: Int,
    val label: String,
) {
    Code4(4, "4-digit code"),
    Code6(6, "6-digit code"),
}