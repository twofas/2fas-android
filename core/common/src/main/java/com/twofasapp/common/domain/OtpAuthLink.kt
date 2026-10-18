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

/**
 * Non-sensitive link parameters for logs. Secret, label, issuer and the raw link are never included.
 */
fun OtpAuthLink.logParams(): String {
    return buildString {
        append("type=${type.take(16)}")
        listOf(OtpAuthLink.ParamDigits, OtpAuthLink.ParamPeriod, OtpAuthLink.ParamAlgorithm).forEach { key ->
            params[key]?.let { append(" $key=${it.take(16)}") }
        }
    }
}