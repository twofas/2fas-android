package com.twofasapp.parsers

import com.twofasapp.di.BackupSyncStatus
import com.twofasapp.parsers.domain.OtpAuthLink
import com.twofasapp.parsers.domain.SupportedService
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.prefs.model.Tint

object LegacyServiceParser {

    fun parseServiceDto(link: OtpAuthLink, supportedService: SupportedService?): ServiceDto {
        val issuer = link.issuer

        var name = supportedService?.name ?: ""
        val account = parseAccount(link.issuer, link.label)

        // If name is still empty, just grab the issuer
        if (name.isBlank()) {
            if (issuer.isNullOrEmpty().not()) {
                name = issuer!!
            } else {
                val (labelPart1, labelPart2) = splitLabel(link.label)
                if (labelPart1.contains("@").not()) {
                    name = labelPart1
                }
            }
        }

        val digits = try {
            link.params[OtpAuthLink.DIGITS_PARAM]?.toInt()
        } catch (e: Exception) {
            null
        }

        val period = try {
            link.params[OtpAuthLink.PERIOD_PARAM]?.toInt()
        } catch (e: Exception) {
            null
        }

        val algorithm = try {
            link.params[OtpAuthLink.ALGORITHM_PARAM]
        } catch (e: Exception) {
            null
        }

        val counter = try {
            link.params[OtpAuthLink.COUNTER]?.toInt()
        } catch (e: Exception) {
            if (link.type.equals("hotp", true)) {
                1
            } else {
                null
            }
        }

        val iconCollectionId = supportedService?.iconCollection?.id ?: ServiceIcons.defaultCollectionId

        return ServiceDto(
            name = name.take(30),
            secret = link.secret,
            authType = ServiceDto.AuthType.valueOf(link.type.uppercase()),
            otpLink = link.link,
            otpLabel = link.label,
            otpAccount = if (name == account) null else account?.take(50),
            otpIssuer = issuer,
            otpDigits = digits,
            otpPeriod = period,
            otpAlgorithm = parseSupportedAlgorithm(algorithm),
            hotpCounter = counter,
            backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
            updatedAt = 0,
            assignedDomains = emptyList(),
            serviceTypeId = supportedService?.id,
            selectedImageType = if (iconCollectionId == ServiceIcons.defaultCollectionId) ServiceDto.ImageType.Label else ServiceDto.ImageType.IconCollection,
            iconCollectionId = iconCollectionId,
            labelText = if (iconCollectionId == ServiceIcons.defaultCollectionId) name.uppercase().take(2) else null,
            labelBackgroundColor = if (iconCollectionId == ServiceIcons.defaultCollectionId) Tint.values().random() else null,
            source = ServiceDto.Source.Link,
        )
    }

    private fun parseAccount(issuer: String?, label: String): String? {
        val (labelPart1, labelPart2) = splitLabel(label)

        return when {
            labelPart2.isNullOrBlank().not() -> labelPart2
            labelPart1.isNotBlank() && labelPart1.contains("@") -> labelPart1
            else -> null
        }
    }

    private fun splitLabel(label: String): Pair<String, String?> {
        val matches = label.split(":".toRegex())

        return when (matches.size) {
            2 -> Pair(matches.first().removeWhiteSpaces(), matches.last().trim())
            else -> Pair(label.removeWhiteSpaces(), null)
        }
    }

    private fun String.removeWhiteSpaces() = this.replace(" ", "")

    private fun parseSupportedAlgorithm(otpAlgorithm: String?): String? =
        when {
            otpAlgorithm == null -> null
            otpAlgorithm.equals("SHA1", ignoreCase = true) -> "SHA1"
            otpAlgorithm.equals("SHA224", ignoreCase = true) -> "SHA224"
            otpAlgorithm.equals("SHA256", ignoreCase = true) -> "SHA256"
            otpAlgorithm.equals("SHA384", ignoreCase = true) -> "SHA384"
            otpAlgorithm.equals("SHA512", ignoreCase = true) -> "SHA512"
            else -> null
        }
}