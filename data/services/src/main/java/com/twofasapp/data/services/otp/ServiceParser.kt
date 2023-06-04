package com.twofasapp.data.services.otp

import com.twofasapp.data.services.domain.Service
import com.twofasapp.di.BackupSyncStatus
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.parsers.SupportedServices
import com.twofasapp.parsers.domain.OtpAuthLink

object ServiceParser {

    fun parseService(link: OtpAuthLink): Service {
        val supportedService = SupportedServices.list.firstOrNull { it.isMatching(link.issuer, link.label) }
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

        return Service(
            id = 0L,
            serviceTypeId = supportedService?.id,
            secret = link.secret,
            code = null,
            name = if (name.isBlank()) "Unknown" else name.take(30),
            info = if (name == account) null else account?.take(50),
            authType = Service.AuthType.valueOf(link.type.uppercase()),
            link = link.link,
            issuer = issuer,
            period = period,
            digits = digits,
            hotpCounter = counter,
            hotpCounterTimestamp = null,
            algorithm = parseSupportedAlgorithm(algorithm),
            groupId = null,
            imageType = if (iconCollectionId == ServiceIcons.defaultCollectionId) Service.ImageType.Label else Service.ImageType.IconCollection,
            iconCollectionId = iconCollectionId,
            iconLight = ServiceIcons.getIcon(iconCollectionId, isDark = false),
            iconDark = ServiceIcons.getIcon(iconCollectionId, isDark = true),
            labelText = if (iconCollectionId == ServiceIcons.defaultCollectionId) name.uppercase().take(2) else null,
            labelColor = if (iconCollectionId == ServiceIcons.defaultCollectionId) Service.Tint.values().random() else null,
            badgeColor = null,
            tags = supportedService?.tags.orEmpty(),
            isDeleted = false,
            updatedAt = System.currentTimeMillis(),
            source = Service.Source.Link,
            assignedDomains = listOf(),
            backupSyncStatus = BackupSyncStatus.NOT_SYNCED

        )
    }

    private fun parseAccount(issuer: String?, label: String): String? {
        val (labelPart1, labelPart2) = splitLabel(label)

        return when {
            labelPart2.isNullOrBlank().not() -> labelPart2
            labelPart1.isNotBlank() && labelPart1 != issuer -> labelPart1
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

    private fun parseSupportedAlgorithm(otpAlgorithm: String?): Service.Algorithm? =
        when {
            otpAlgorithm == null -> null
            otpAlgorithm.equals("SHA1", ignoreCase = true) -> Service.Algorithm.SHA1
            otpAlgorithm.equals("SHA224", ignoreCase = true) -> Service.Algorithm.SHA224
            otpAlgorithm.equals("SHA256", ignoreCase = true) -> Service.Algorithm.SHA256
            otpAlgorithm.equals("SHA384", ignoreCase = true) -> Service.Algorithm.SHA384
            otpAlgorithm.equals("SHA512", ignoreCase = true) -> Service.Algorithm.SHA512
            else -> null
        }
}