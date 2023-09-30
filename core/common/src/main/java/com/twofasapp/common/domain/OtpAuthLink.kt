package com.twofasapp.common.domain

data class OtpAuthLink(
    val type: String,
    val label: String,
    val secret: String,
    val issuer: String?,
    val params: Map<String, String> = emptyMap(),
    val link: String?,
) {
    companion object {
        const val ParamDigits = "digits"
        const val ParamPeriod = "period"
        const val ParamAlgorithm = "algorithm"
        const val ParamCounter = "counter"
    }
}