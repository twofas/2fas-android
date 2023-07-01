package com.twofasapp.data.services.domain

import com.twofasapp.di.BackupSyncStatus

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
) {
    data class Code(
        val current: String,
        val next: String,
        val timer: Int,
        val progress: Float,
    )

    enum class AuthType { TOTP, HOTP }
    enum class Algorithm { SHA1, SHA224, SHA256, SHA384, SHA512 }
    enum class ImageType { IconCollection, Label }
    enum class Source { Link, Manual }
    enum class Tint {
        Default,
        LightBlue,
        Indigo,
        Purple,
        Turquoise,
        Green,
        Red,
        Orange,
        Yellow;
    }

    companion object {
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
    }
}
