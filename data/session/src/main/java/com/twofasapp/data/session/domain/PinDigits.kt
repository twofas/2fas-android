package com.twofasapp.data.session.domain

enum class PinDigits(
    val value: Int,
    val label: Int,
) {
    Code4(4, com.twofasapp.locale.R.string.settings__pin_4_digits),
    Code6(6, com.twofasapp.locale.R.string.settings__pin_6_digits),
}