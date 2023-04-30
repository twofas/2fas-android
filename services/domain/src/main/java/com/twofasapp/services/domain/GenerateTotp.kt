package com.twofasapp.services.domain

import com.twofasapp.common.time.TimeProvider
import com.twofasapp.services.domain.otp.OtpAuthenticator
import com.twofasapp.services.domain.otp.OtpData


class GenerateTotp(
    private val timeProvider: TimeProvider,
) {

    companion object {
        private const val DEFAULT_DIGITS = 6
        private val DEFAULT_ALGORITHM = OtpData.Algorithm.SHA1
    }

    private val authenticator = OtpAuthenticator()

    fun calculateCode(secret: String, otpDigits: Int?, otpPeriod: Int, otpAlgorithm: String?, counter: Long?): String {
        return authenticator.createOneTimePassword(
            OtpData(
                nowMillis = timeProvider.realCurrentTime(),
                counter = counter,
                secret = secret,
                digits = otpDigits ?: DEFAULT_DIGITS,
                period = otpPeriod,
                algorithm = getAlgorithm(otpAlgorithm),
            )
        )
    }

    fun calculateNextCode(secret: String, otpDigits: Int?, otpPeriod: Int, otpAlgorithm: String?, counter: Long? = null): String {
        return authenticator.createOneTimePassword(
            OtpData(
                nowMillis = timeProvider.realCurrentTime() + otpPeriod.toLong() * 1_000L,
                counter = counter,
                secret = secret,
                digits = otpDigits ?: DEFAULT_DIGITS,
                period = otpPeriod,
                algorithm = getAlgorithm(otpAlgorithm),
            )
        )
    }

    private fun getAlgorithm(otpAlgorithm: String?) =
        when {
            otpAlgorithm == null -> DEFAULT_ALGORITHM
            otpAlgorithm.equals("SHA1", ignoreCase = true) -> OtpData.Algorithm.SHA1
            otpAlgorithm.equals("SHA224", ignoreCase = true) -> OtpData.Algorithm.SHA224
            otpAlgorithm.equals("SHA256", ignoreCase = true) -> OtpData.Algorithm.SHA256
            otpAlgorithm.equals("SHA384", ignoreCase = true) -> OtpData.Algorithm.SHA384
            otpAlgorithm.equals("SHA512", ignoreCase = true) -> OtpData.Algorithm.SHA512
            else -> DEFAULT_ALGORITHM
        }
}