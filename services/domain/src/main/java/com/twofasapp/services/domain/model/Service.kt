package com.twofasapp.services.domain.model

import com.twofasapp.di.BackupSyncStatus
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.prefs.model.Tint

data class Service(
    val id: Long = 0,
    val name: String,
    val secret: String,
    val authType: AuthType = AuthType.TOTP,
    val otp: Otp = Otp(),
    val badge: Badge? = null,
    val selectedImageType: ImageType = ImageType.IconCollection,
    val labelText: String? = null,
    val labelBackgroundColor: Tint? = null,
    val iconCollectionId: String,
    val groupId: String? = null,
    val assignedDomains: List<String> = emptyList(),
    val isDeleted: Boolean = false,
    val backupSyncStatus: BackupSyncStatus = BackupSyncStatus.NOT_SYNCED,
    var updatedAt: Long = 0,
    val serviceTypeId: String?,
    val source: Source,
    val tags: List<String> = emptyList(),
) {

    companion object {
        const val DefaultPeriod = 30
        const val DefaultDigits = 6
        const val DefaultHotpCounter = 1
        val DefaultAlgorithm = Algorithm.SHA1
        val DefaultAuthType = AuthType.TOTP
        val DefaultSource = Source.Manual

        fun createDefault() = Service(
            id = 0,
            name = "",
            secret = "",
            iconCollectionId = ServiceIcons.defaultCollectionId,
            serviceTypeId = null,
            source = Source.Manual,
            selectedImageType = ImageType.Label,
            labelBackgroundColor = Tint.values().random(),
        )
    }

    enum class AuthType { TOTP, HOTP }
    enum class Algorithm { SHA1, SHA224, SHA256, SHA384, SHA512 }
    enum class ImageType { IconCollection, Label }
    enum class Source { Link, Manual }

    data class Otp(
        val link: String? = null,
        val label: String = "",
        val account: String = "",
        val issuer: String? = null,
        val digits: Int = DefaultDigits,
        val period: Int = DefaultPeriod,
        val hotpCounter: Int? = null,
        val algorithm: Algorithm = DefaultAlgorithm,
    )

    data class Badge(
        val color: Tint,
    )
}
