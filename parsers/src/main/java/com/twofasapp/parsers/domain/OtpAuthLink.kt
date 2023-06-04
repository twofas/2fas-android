package com.twofasapp.parsers.domain

data class OtpAuthLink(
    val type: String,
    val label: String,
    val secret: String,
    val issuer: String?,
    val params: Map<String, String> = emptyMap(),
    val link: String?,
) {
    companion object {
        const val DIGITS_PARAM = "digits"
        const val PERIOD_PARAM = "period"
        const val ALGORITHM_PARAM = "algorithm"
        const val COUNTER = "counter"
    }
}