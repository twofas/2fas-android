package com.twofasapp.data.services.otp

import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.services.domain.Service
import com.twofasapp.otp.OtpAuthenticator
import com.twofasapp.otp.OtpData
import java.time.Instant

class ServiceCodeGenerator(
    private val timeProvider: TimeProvider,
) {
    private val authenticator = OtpAuthenticator()

    fun generate(service: Service): Service {
        val period = service.period ?: 30
        val digits = service.digits ?: 6
        val algorithm = when (service.algorithm) {
            Service.Algorithm.SHA1 -> OtpData.Algorithm.SHA1
            Service.Algorithm.SHA224 -> OtpData.Algorithm.SHA224
            Service.Algorithm.SHA256 -> OtpData.Algorithm.SHA256
            Service.Algorithm.SHA384 -> OtpData.Algorithm.SHA384
            Service.Algorithm.SHA512 -> OtpData.Algorithm.SHA512
            null -> OtpData.Algorithm.SHA1
        }

        // TODO: Handle HOTP
        val currentCounter = timeProvider.systemCurrentTime() / period.toMillis()
        val nextCounter = (timeProvider.systemCurrentTime() + period.toMillis()) / period.toMillis()

        val otpData = OtpData(
            counter = currentCounter,
            secret = service.secret,
            digits = digits,
            period = period,
            algorithm = algorithm,
        )

        val timer = calculateTimer(period)

        return service.copy(
            code = Service.Code(
                current = authenticator.generateOtpCode(otpData),
                next = authenticator.generateOtpCode(otpData.copy(counter = nextCounter)),
                timer = timer,
                progress = timer / period.toFloat()
            )
        )
    }

    private fun calculateTimer(period: Int): Int {
//        return 30 - (Instant.now().epochSecond + timeProvider.realTimeDelta() / 1000) % 30
        return (period - (Instant.now().epochSecond) % period).toInt()

    }

    private fun Int.toMillis(): Long {
        return this * 1000L
    }
}