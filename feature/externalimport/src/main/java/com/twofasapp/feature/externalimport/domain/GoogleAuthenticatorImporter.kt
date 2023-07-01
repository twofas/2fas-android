package com.twofasapp.feature.externalimport.domain

import android.net.Uri
import com.twofasapp.GoogleAuthenticatorProto.MigrationPayload
import com.twofasapp.core.encoding.decodeBase64ToByteArray
import com.twofasapp.extensions.doNothing
import com.twofasapp.parsers.domain.OtpAuthLink
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.services.domain.ConvertOtpLinkToService
import org.apache.commons.codec.binary.Base32

class GoogleAuthenticatorImporter(
    private val convertOtpLinkToService: ConvertOtpLinkToService,
) : ExternalImporter {

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
            val uri = Uri.parse(content)

            if (uri.host.equals(HOST, true).not()) {
                return ExternalImport.UnsupportedError
            }

            val dataEncoded = uri.getQueryParameter(DATA) ?: return ExternalImport.UnsupportedError
            val data = dataEncoded.decodeBase64ToByteArray()

            val proto = MigrationPayload.parseFrom(data)
            val totalServices = proto.otpParametersCount
            val servicesToImport = mutableListOf<ServiceDto>()
            proto.otpParametersList.forEach {
                if (isTypeSupported(it)) {
                    servicesToImport.add(parseService(it))
                }
            }

            return ExternalImport.Success(
                servicesToImport = servicesToImport,
                totalServicesCount = totalServices,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return ExternalImport.ParsingError(e)
        }
    }

    private fun parseService(otpParameters: MigrationPayload.OtpParameters): ServiceDto {
        val label = if (otpParameters.name.contains(":")) otpParameters.name else "${otpParameters.name}:"

        val otpLink = com.twofasapp.parsers.domain.OtpAuthLink(
            type = parseType(otpParameters.type),
            label = label,
            secret = Base32().encodeAsString(otpParameters.secret.toByteArray()),
            issuer = otpParameters.issuer,
            params = parseParams(otpParameters),
            link = null,
        )

        val parsed = convertOtpLinkToService.execute(otpLink)

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
            MigrationPayload.DigitCount.DIGIT_COUNT_UNSPECIFIED -> doNothing()
            MigrationPayload.DigitCount.DIGIT_COUNT_SIX -> params[DIGITS] = "6"
            MigrationPayload.DigitCount.DIGIT_COUNT_EIGHT -> params[DIGITS] = "8"
            MigrationPayload.DigitCount.UNRECOGNIZED -> doNothing()
            else -> doNothing()
        }

        when (otpParameters.algorithm) {
            MigrationPayload.Algorithm.ALGORITHM_SHA1 -> params[ALGORITHM] = "SHA1"
            MigrationPayload.Algorithm.ALGORITHM_SHA256 -> params[ALGORITHM] = "SHA256"
            MigrationPayload.Algorithm.ALGORITHM_SHA512 -> params[ALGORITHM] = "SHA512"
            MigrationPayload.Algorithm.ALGORITHM_UNSPECIFIED,
            MigrationPayload.Algorithm.ALGORITHM_MD5,
            MigrationPayload.Algorithm.UNRECOGNIZED -> doNothing()

            else -> doNothing()
        }

        return params
    }
}