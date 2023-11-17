package com.twofasapp.otp

data class OtpData(
    val counter: Long,
    val secret: String,
    val digits: Int,
    val period: Int,
    val algorithm: Algorithm,
    val calculateModule: Boolean, // Do not calculate for Steam codes
) {
    enum class Algorithm { SHA1, SHA224, SHA256, SHA384, SHA512 }
}