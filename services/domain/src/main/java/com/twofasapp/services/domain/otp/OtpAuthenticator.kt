/*
 * Copyright (c) 2014-2018 Enrico M. Crisostomo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   * Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *   * Neither the name of the author nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.twofasapp.services.domain.otp

import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.time.Duration
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and
import kotlin.math.pow

/**
 * This class implements the functionality described in RFC 6238 (TOTP: Time
 * based one-time password algorithm) and has been tested again Google's
 * implementation of such algorithm in its Google Authenticator application.
 * <p>
 * This class lets users create a new 16-bit base32-encoded secret key with
 * the validation code calculated at {@code time = 0} (the UNIX epoch) and the
 * URL of a Google-provided QR barcode to let an user load the generated
 * information into Google Authenticator.
 * <p>
 * The random number generator used by this class uses the default algorithm and
 * provider.  Users can override them by setting the following system properties
 * to the algorithm and provider name of their choice:
 * <ul>
 * <li>{@link #RNG_ALGORITHM}.</li>
 * <li>{@link #RNG_ALGORITHM_PROVIDER}.</li>
 * </ul>
 * <p>
 * This class does not store in any way either the generated keys nor the keys
 * passed during the authorization process.
 * <p>
 * Java Server side class for Google Authenticator's TOTP generator was inspired
 * by an author's blog post.
 *
 * @author Enrico M. Crisostomo
 * @author Warren Strange
 * @version 1.1.4
 * @see <a href="http://thegreyblog.blogspot.com/2011/12/google-authenticator-using-it-in-your.html"></a>
 * @see <a href="http://code.google.com/p/google-authenticator"></a>
 * @see <a href="http://tools.ietf.org/id/draft-mraihi-totp-timebased-06.txt"></a>
 * @since 0.3.0
 */
class OtpAuthenticator {

    companion object {
        private const val HmacSha1 = "HmacSHA1"
        private const val HmacSha224 = "HmacSHA224"
        private const val HmacSha256 = "HmacSHA256"
        private const val HmacSha384 = "HmacSHA384"
        private const val HmacSha512 = "HmacSHA512"
        private val keyModulus = mapOf(
            6 to 10.0.pow(6.toDouble()).toLong(),
            7 to 10.0.pow(7.toDouble()).toLong(),
            8 to 10.0.pow(8.toDouble()).toLong(),
        )
    }

    fun createOneTimePassword(otpData: OtpData): String {
        val code = calculateCode(
            key = TOTPSecretKey.from(TOTPSecretKey.KeyRepresentation.BASE32, otpData.secret).value,
            counter = otpData.counter ?: (otpData.nowMillis / Duration.ofSeconds(otpData.period.toLong()).toMillis()),
            digits = otpData.digits,
            algorithm = when (otpData.algorithm) {
                OtpData.Algorithm.SHA1 -> HmacSha1
                OtpData.Algorithm.SHA224 -> HmacSha224
                OtpData.Algorithm.SHA256 -> HmacSha256
                OtpData.Algorithm.SHA384 -> HmacSha384
                OtpData.Algorithm.SHA512 -> HmacSha512
            }
        )

        return String.format("%0${otpData.digits}d", code)
    }

    private fun calculateCode(
        key: ByteArray,
        counter: Long,
        digits: Int,
        algorithm: String,
    ): Int {
        val bigEndianTimestamp = ByteArray(8)
        var value = counter
        var byte = 8
        while (byte-- > 0) {
            bigEndianTimestamp[byte] = value.toByte()
            value = value ushr 8
        }

        val signKey = SecretKeySpec(key, algorithm)

        try {
            val mac = Mac.getInstance(algorithm)
            mac.init(signKey)

            val hash = mac.doFinal(bigEndianTimestamp)
            val offset = hash[hash.size - 1] and 0xF

            var truncatedHash: Long = 0

            for (i in 0..3) {
                truncatedHash = truncatedHash shl 8
                truncatedHash = truncatedHash or (hash[offset + i].toInt() and 0xFF).toLong()
            }

            truncatedHash = truncatedHash and 0x7FFFFFFF
            truncatedHash %= keyModulus[digits]!!

            return truncatedHash.toInt()
        } catch (e: NoSuchAlgorithmException) {
            throw TOTPException("The operation cannot be performed now.", e)
        } catch (e: InvalidKeyException) {
            throw TOTPException("The operation cannot be performed now.", e)
        }
    }
}
