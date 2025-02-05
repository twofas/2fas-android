package com.twofasapp.common.domain

import android.net.Uri

data class Service(
    val id: Long,
    val serviceTypeId: String?,
    val secret: String,
    val code: Code? = null,
    val name: String,
    val info: String?,
    val authType: AuthType,
    val link: String?,
    val issuer: String?,
    val period: Int?,
    val digits: Int?,
    val hotpCounter: Int? = null,
    val hotpCounterTimestamp: Long? = null,
    val algorithm: Algorithm?,
    val groupId: String? = null,
    val imageType: ImageType = ImageType.IconCollection,
    val iconCollectionId: String,
    val iconLight: String,
    val iconDark: String,
    val labelText: String? = null,
    val labelColor: Tint? = null,
    val badgeColor: Tint?,
    val tags: List<String>,
    val isDeleted: Boolean = false,
    val updatedAt: Long = 0,
    val source: Source,
    val assignedDomains: List<String> = emptyList(),
    val backupSyncStatus: BackupSyncStatus,
    val revealTimestamp: Long? = null,
) {
    data class Code(
        val current: String,
        val next: String,
        val timer: Int,
        val progress: Float,
    )

    enum class AuthType { TOTP, HOTP, STEAM }
    enum class Algorithm { SHA1, SHA224, SHA256, SHA384, SHA512 }
    enum class ImageType { IconCollection, Label }
    enum class Source { Link, Manual }
    enum class Tint {
        Default,
        Red,
        Orange,
        Yellow,
        Green,
        Turquoise,
        LightBlue,
        Indigo,
        Pink,
        Purple,
        Brown,
        ;
    }

    companion object {
        val DefaultAuthType = AuthType.TOTP
        val DefaultAlgorithm = Algorithm.SHA1
        const val DefaultPeriod = 30
        const val DefaultDigits = 6

        val Empty = Service(
            id = 0L,
            serviceTypeId = null,
            secret = "",
            code = null,
            name = "",
            info = null,
            authType = AuthType.TOTP,
            link = null,
            issuer = null,
            period = null,
            digits = null,
            hotpCounter = null,
            hotpCounterTimestamp = null,
            algorithm = null,
            groupId = null,
            imageType = ImageType.IconCollection,
            iconCollectionId = "",
            iconLight = "",
            iconDark = "",
            labelText = null,
            labelColor = null,
            badgeColor = null,
            tags = listOf(),
            isDeleted = false,
            updatedAt = 0L,
            source = Source.Link,
            assignedDomains = listOf(),
            backupSyncStatus = BackupSyncStatus.SYNCED,
        )

        val Preview = Service(
            id = 0L,
            serviceTypeId = null,
            secret = "QWE",
            code = Code(current = "123456", next = "456789", timer = 20, progress = 0.66666f),
            name = "Test Service",
            info = "test@mail.com",
            authType = AuthType.TOTP,
            link = null,
            issuer = null,
            period = 30,
            digits = 6,
            hotpCounter = null,
            hotpCounterTimestamp = null,
            algorithm = Algorithm.SHA1,
            groupId = null,
            imageType = ImageType.Label,
            iconCollectionId = "a5b3fb65-4ec5-43e6-8ec1-49e24ca9e7ad",
            iconLight = "38e26d32-3c76-4768-8e12-89a050676a07",
            iconDark = "38e26d32-3c76-4768-8e12-89a050676a07",
            labelText = "TS",
            labelColor = Tint.LightBlue,
            badgeColor = null,
            tags = listOf(),
            isDeleted = false,
            updatedAt = 0L,
            source = Source.Link,
            assignedDomains = listOf(),
            backupSyncStatus = BackupSyncStatus.SYNCED,
        )
    }

    fun toUri(): String {
        return Uri.Builder()
            .apply {

                scheme("otpauth")

                authority(authType.name.lowercase())

                if (info.isNullOrBlank().not()) {
                    appendEncodedPath("$name:$info")
                } else {
                    appendEncodedPath(name)
                }

                appendQueryParameter("secret", secret)

                if (issuer.isNullOrBlank().not()) {
                    appendQueryParameter("issuer", issuer)
                }

                if (digits != null && digits != DefaultDigits) {
                    appendQueryParameter("digits", digits.toString())
                }

                if (period != null && period != DefaultPeriod) {
                    appendQueryParameter("period", period.toString())
                }

                if (hotpCounter != null && authType == AuthType.HOTP) {
                    appendQueryParameter("counter", hotpCounter.toString())
                }

                if (algorithm != null && algorithm != DefaultAlgorithm) {
                    appendQueryParameter("algorithm", algorithm.name)
                }

            }
            .toString()
    }
}