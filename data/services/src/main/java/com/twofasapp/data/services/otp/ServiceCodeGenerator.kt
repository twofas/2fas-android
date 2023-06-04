package com.twofasapp.data.services.otp

import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.services.domain.Service
import com.twofasapp.otp.OtpAuthenticator
import com.twofasapp.otp.OtpData
import timber.log.Timber
import java.time.Instant

class ServiceCodeGenerator(
    private val timeProvider: TimeProvider,
) {
    private val authenticator = OtpAuthenticator()

    fun check(secret: String): Boolean {
        return try {
            authenticator.generateOtpCode(
                OtpData(
                    counter = timeProvider.realCurrentTime() / 30.toMillis(),
                    secret = secret,
                    digits = 6,
                    period = 30,
                    algorithm = OtpData.Algorithm.SHA1,
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
        )

        val timerLeftMillis = periodMillis - timeProvider.realCurrentTime() % periodMillis
        val timer = timerLeftMillis / 1000 + 1 // show 1 as the last number instead of 0, just for nicer UI
        val progress = timer / period.toFloat()

        return service.copy(
            code = Service.Code(
                current = authenticator.generateOtpCode(otpData),
                next = authenticator.generateOtpCode(otpData.copy(counter = nextCounter)),
                timer = timer.toInt(),
                progress = progress,
            )
        )
    }

    // To be removed
    private fun calculateTimer(period: Int): Int {
        return period - ((Instant.now().epochSecond + timeProvider.realTimeDelta() / 1000) % period).toInt()
    }

    private fun Int.toMillis(): Long {
        return this * 1000L
    }
}