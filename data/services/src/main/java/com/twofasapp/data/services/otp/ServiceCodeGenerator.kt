package com.twofasapp.data.services.otp

import com.twofasapp.common.domain.Service
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.otp.OtpAuthenticator
import com.twofasapp.otp.OtpData
import timber.log.Timber

class ServiceCodeGenerator(
    private val timeProvider: TimeProvider,
) {
    companion object {
        private const val SteamCharset = "23456789BCDFGHJKMNPQRTVWXY"
        private const val SteamDigits = 5
    }

    private val authenticator = OtpAuthenticator()

    fun check(secret: String): Boolean {
        return check(
            secret = secret,
            digits = 6,
            period = 30,
            algorithm = Service.Algorithm.SHA1
        )
    }

    fun check(
        secret: String,
        digits: Int,
        period: Int,
        algorithm: Service.Algorithm,
    ): Boolean {
        return try {
            authenticator.generateOtpCode(
                OtpData(
                    counter = timeProvider.realCurrentTime() / 30.toMillis(),
                    secret = secret,
                    digits = digits,
                    period = period,
                    algorithm = when (algorithm) {
                        Service.Algorithm.SHA1 -> OtpData.Algorithm.SHA1
                        Service.Algorithm.SHA224 -> OtpData.Algorithm.SHA224
                        Service.Algorithm.SHA256 -> OtpData.Algorithm.SHA256
                        Service.Algorithm.SHA384 -> OtpData.Algorithm.SHA384
                        Service.Algorithm.SHA512 -> OtpData.Algorithm.SHA512
                    },
                    calculateModule = true,
                )
            )
            true
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    fun generate(service: Service): Service {
        val period = service.period ?: 30
        val periodMillis = period * 1000
        val digits = service.digits ?: 6
        val algorithm = when (service.algorithm) {
            Service.Algorithm.SHA1 -> OtpData.Algorithm.SHA1
            Service.Algorithm.SHA224 -> OtpData.Algorithm.SHA224
            Service.Algorithm.SHA256 -> OtpData.Algorithm.SHA256
            Service.Algorithm.SHA384 -> OtpData.Algorithm.SHA384
            Service.Algorithm.SHA512 -> OtpData.Algorithm.SHA512
            null -> OtpData.Algorithm.SHA1
        }

        var currentCounter = 0L
        var nextCounter = 0L

        when (service.authType) {
            Service.AuthType.STEAM,
            Service.AuthType.TOTP -> {
                currentCounter = timeProvider.realCurrentTime() / period.toMillis()
                nextCounter = (timeProvider.realCurrentTime() + period.toMillis()) / period.toMillis()
            }

            Service.AuthType.HOTP -> {
                currentCounter = (service.hotpCounter ?: 1).toLong()
                nextCounter = (service.hotpCounter ?: 1) + 1L
            }
        }


        val otpData = OtpData(
            counter = currentCounter,
            secret = service.secret,
            digits = digits,
            period = period,
            algorithm = algorithm,
            calculateModule = when (service.authType) {
                Service.AuthType.TOTP -> true
                Service.AuthType.HOTP -> true
                Service.AuthType.STEAM -> false
            },
        )

        val timerLeftMillis = periodMillis - timeProvider.realCurrentTime() % periodMillis
        val timer = timerLeftMillis / 1000 + 1 // show 1 as the last number instead of 0, just for nicer UI
        val progress = timer / period.toFloat()

        var currentCode = authenticator.generateOtpCode(otpData)
        var nextCode = authenticator.generateOtpCode(otpData.copy(counter = nextCounter))

        when (service.authType) {
            Service.AuthType.TOTP -> Unit
            Service.AuthType.HOTP -> Unit
            Service.AuthType.STEAM -> {
                currentCode = generateSteamCode(currentCode)
                nextCode = generateSteamCode(nextCode)
            }
        }

        return service.copy(
            code = Service.Code(
                current = currentCode,
                next = nextCode,
                timer = timer.toInt(),
                progress = progress,
            )
        )
    }

    private fun Int.toMillis(): Long {
        return this * 1000L
    }

    private fun generateSteamCode(code: String): String {
        var numericCode = code.toInt()

        return buildString {
            repeat(SteamDigits) {
                append(SteamCharset[numericCode % SteamCharset.length])
                numericCode /= SteamCharset.length
            }
        }
    }
}