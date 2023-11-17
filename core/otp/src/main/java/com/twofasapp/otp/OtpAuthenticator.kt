package com.twofasapp.otp

import org.apache.commons.codec.binary.Base32
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and
import kotlin.math.pow

/**
 * This class implements the functionality described in RFC 6238 (TOTP: Time
 * based one-time password algorithm) and has been tested again Google's
 * implementation of such algorithm in its Google Authenticator application.
 *
 * This class lets users create a new 16-bit base32-encoded secret key with
 * the validation code calculated at `time = 0` (the UNIX epoch) and the
 * URL of a Google-provided QR barcode to let an user load the generated
 * information into Google Authenticator.
 *
 * Java Server side class for Google Authenticator's TOTP generator was inspired by an author's blog post.
 *
 * @see [Blog Post](http://thegreyblog.blogspot.com/2011/12/google-authenticator-using-it-in-your.html)
 * @see [Google Authenticator](http://code.google.com/p/google-authenticator)
 * @see [HOTP Time Based](http://tools.ietf.org/id/draft-mraihi-totp-timebased-06.txt)
 */
class OtpAuthenticator {

    companion object {
        private const val HmacSha1 = "HmacSHA1"
        private const val HmacSha224 = "HmacSHA224"
        private const val HmacSha256 = "HmacSHA256"
        private const val HmacSha384 = "HmacSHA384"
        private const val HmacSha512 = "HmacSHA512"
        private val keyModulus = mapOf(
            5 to 10.0.pow(5.toDouble()).toLong(),
            6 to 10.0.pow(6.toDouble()).toLong(),
            7 to 10.0.pow(7.toDouble()).toLong(),
            8 to 10.0.pow(8.toDouble()).toLong(),
        )
    }

    fun generateOtpCode(otpData: OtpData): String {
        val code = calculateCode(
            key = Base32().decode(otpData.secret),
            counter = otpData.counter, // ?: (otpData.nowMillis / Duration.ofSeconds(otpData.period.toLong()).toMillis()),
            digits = otpData.digits,
            algorithm = when (otpData.algorithm) {
                OtpData.Algorithm.SHA1 -> HmacSha1
                OtpData.Algorithm.SHA224 -> HmacSha224
                OtpData.Algorithm.SHA256 -> HmacSha256
                OtpData.Algorithm.SHA384 -> HmacSha384
                OtpData.Algorithm.SHA512 -> HmacSha512
            },
            calculateModule = otpData.calculateModule,
        )

        return String.format("%0${otpData.digits}d", code)
    }

    /**
     * Calculates the verification code of the provided key at the specified
     * instant of time using the algorithm specified in RFC 6238.
     *
     * @param key the secret key in binary format.
     * @param counter the instant of time.
     * @return the validation code for the provided key at the specified instant of time.
     */
    private fun calculateCode(
        key: ByteArray,
        counter: Long,
        digits: Int,
        algorithm: String,
        calculateModule: Boolean,
    ): Int {
        // Converting the instant of time from the long representation to a  big-endian array of bytes (RFC4226, 5.2. Description).
        val bigEndianTimestamp = ByteArray(8)
        var value = counter
        var byte = 8
        while (byte-- > 0) {
            bigEndianTimestamp[byte] = value.toByte()
            value = value ushr 8
        }

        // Building the secret key specification for the HmacSHA1 algorithm.
        val signKey = SecretKeySpec(key, algorithm)

        try {
            // Getting an HmacSHA1 algorithm implementation from the JCE.
            val mac = Mac.getInstance(algorithm)
            mac.init(signKey)

            // Processing the instant of time and getting the encrypted data.
            val hash = mac.doFinal(bigEndianTimestamp)

            // Building the validation code performing dynamic truncation (RFC4226, 5.3. Generating an HOTP value)
            val offset = hash[hash.size - 1] and 0xF

            // We are using a long because Java hasn't got an unsigned integer type and we need 32 unsigned bits).
            var truncatedHash: Long = 0

            for (i in 0..3) {
                truncatedHash = truncatedHash shl 8

                // Java bytes are signed but we need an unsigned integer: cleaning off all but the LSB.
                truncatedHash = truncatedHash or (hash[offset + i].toInt() and 0xFF).toLong()
            }

            // Clean bits higher than the 32nd (inclusive) and calculate the module with the maximum validation code value.
            return if (calculateModule) {
                truncatedHash = truncatedHash and 0x7FFFFFFF
                truncatedHash %= keyModulus[digits]!!
                truncatedHash.toInt()
            } else {
                truncatedHash = truncatedHash and 0x7FFFFFFF
                truncatedHash.toInt()
            }

        } catch (e: NoSuchAlgorithmException) {
            throw OtpException("The operation cannot be performed now.", e)
        } catch (e: InvalidKeyException) {
            throw OtpException("The operation cannot be performed now.", e)
        }
    }
}
