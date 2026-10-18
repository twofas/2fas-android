package com.twofasapp.feature.externalimport.domain

import android.net.Uri
import com.twofasapp.GoogleAuthenticatorProto.MigrationPayload
import com.twofasapp.common.domain.OtpAuthLink
import com.twofasapp.common.domain.Service
import com.twofasapp.common.ktx.decodeBase64
import com.twofasapp.common.ktx.legacyDecodeBase64ToByteArray
import com.twofasapp.data.services.otp.ServiceParser
import org.apache.commons.codec.binary.Base32

class GoogleAuthenticatorImporter : ExternalImporter {

    companion object {
        private const val SCHEMA = "otpauth-migration"
        private const val HOST = "offline"
        private const val DATA = "data"
        private const val DIGITS = "digits"
        private const val ALGORITHM = "algorithm"
    }

    override fun isSchemaSupported(content: String): Boolean {
        return content.startsWith(SCHEMA)
    }

    override fun read(content: String): ExternalImport {
        try {
            val payloads = content.lines().filter { it.isNotBlank() }.map { line ->
                when (val decoded = decodePayload(line)) {
                    is DecodedPayload.Success -> decoded.payload
                    is DecodedPayload.Unsupported -> return ExternalImport.UnsupportedError(decoded.reason)
                }
            }

            val otpParameters = payloads.flatMap { it.otpParametersList }
            val servicesToImport = mutableListOf<Service?>()
            otpParameters.forEach {
                if (isTypeSupported(it)) {
                    servicesToImport.add(parseService(it))
                }
            }

            return ExternalImport.Success(
                servicesToImport = servicesToImport.filterNotNull(),
                totalServicesCount = otpParameters.size,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return ExternalImport.ParsingError(e)
        }
    }

    fun readBatch(content: String): GoogleAuthenticatorBatch? {
        return try {
            when (val decoded = decodePayload(content)) {
                is DecodedPayload.Success -> GoogleAuthenticatorBatch(
                    size = decoded.payload.batchSize,
                    index = decoded.payload.batchIndex,
                    id = decoded.payload.batchId,
                    servicesCount = decoded.payload.otpParametersCount,
                )

                is DecodedPayload.Unsupported -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun decodePayload(content: String): DecodedPayload {
        val uri = Uri.parse(content.trim())

        if (uri.host.equals(HOST, true).not()) {
            return DecodedPayload.Unsupported("Unsupported host: ${uri.host}")
        }

        val dataEncoded = uri.getQueryParameter(DATA) ?: return DecodedPayload.Unsupported("Missing $DATA parameter")

        return DecodedPayload.Success(MigrationPayload.parseFrom(dataEncoded.decodeBase64()))
    }

    private fun parseService(otpParameters: MigrationPayload.OtpParameters): Service {
        val label = if (otpParameters.name.contains(":")) otpParameters.name else "${otpParameters.name}:"

        val otpLink = OtpAuthLink(
            type = parseType(otpParameters.type),
            label = label,
            secret = Base32().encodeAsString(otpParameters.secret.toByteArray()),
            issuer = otpParameters.issuer,
            params = parseParams(otpParameters),
            link = null,
        )

        val parsed = ServiceParser.parseService(otpLink)

        return if (parsed.name.isBlank()) parsed.copy(name = label.split(":")[0]) else parsed
    }

    private fun isTypeSupported(otpParameters: MigrationPayload.OtpParameters): Boolean {
        return when (otpParameters.type) {
            MigrationPayload.OtpType.OTP_TYPE_UNSPECIFIED -> true
            MigrationPayload.OtpType.OTP_TYPE_HOTP -> true
            MigrationPayload.OtpType.OTP_TYPE_TOTP -> true
            MigrationPayload.OtpType.UNRECOGNIZED -> false
            else -> false
        }
    }

    private fun parseType(type: MigrationPayload.OtpType): String {
        return when (type) {
            MigrationPayload.OtpType.OTP_TYPE_HOTP -> "HOTP"
            MigrationPayload.OtpType.OTP_TYPE_TOTP -> "TOTP"
            MigrationPayload.OtpType.OTP_TYPE_UNSPECIFIED -> "TOTP"
            MigrationPayload.OtpType.UNRECOGNIZED -> "TOTP"
        }
    }

    private fun parseParams(otpParameters: MigrationPayload.OtpParameters): Map<String, String> {
        val params = mutableMapOf<String, String>()

        when (otpParameters.digits) {
            MigrationPayload.DigitCount.DIGIT_COUNT_UNSPECIFIED -> Unit
            MigrationPayload.DigitCount.DIGIT_COUNT_SIX -> params[DIGITS] = "6"
            MigrationPayload.DigitCount.DIGIT_COUNT_EIGHT -> params[DIGITS] = "8"
            MigrationPayload.DigitCount.UNRECOGNIZED -> Unit
            else -> Unit
        }

        when (otpParameters.algorithm) {
            MigrationPayload.Algorithm.ALGORITHM_SHA1 -> params[ALGORITHM] = "SHA1"
            MigrationPayload.Algorithm.ALGORITHM_SHA256 -> params[ALGORITHM] = "SHA256"
            MigrationPayload.Algorithm.ALGORITHM_SHA512 -> params[ALGORITHM] = "SHA512"
            MigrationPayload.Algorithm.ALGORITHM_UNSPECIFIED,
            MigrationPayload.Algorithm.ALGORITHM_MD5,
            MigrationPayload.Algorithm.UNRECOGNIZED,
            -> Unit

            else -> Unit
        }

        return params
    }

    private sealed interface DecodedPayload {
        data class Success(val payload: MigrationPayload) : DecodedPayload
        data class Unsupported(val reason: String) : DecodedPayload
    }
}