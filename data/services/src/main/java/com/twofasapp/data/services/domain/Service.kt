package com.twofasapp.data.services.domain

data class Service(
    val id: Long,
    val secret: String,
    val code: Code? = null,
    val name: String,
    val info: String?,
    val authType: AuthType,
    val period: Int?,
    val digits: Int?,
    val algorithm: Algorithm?,
    val groupId: String? = null,
    val imageType: ImageType = ImageType.IconCollection,
    val iconCollectionId: String,
    val iconLight: String,
    val iconDark: String,
    val labelText: String? = null,
    val labelColor: Tint? = null,
    val badgeColor: Tint?,

    val isDeleted: Boolean = false,


//    val otp: Otp = Otp(),

////
//    val assignedDomains: List<String> = emptyList(),
//    val isDeleted: Boolean = false,
////    val backupSyncStatus: BackupSyncStatus = BackupSyncStatus.NOT_SYNCED,
//    var updatedAt: Long = 0,
//    val serviceTypeId: String?,
//    val source: Source,
//    val tags: List<String> = emptyList(),
) {

//

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
//    companion object {
//        const val DefaultPeriod = 30
//        const val DefaultDigits = 6
//        const val DefaultHotpCounter = 1
//        val DefaultAlgorithm = OtpData.Algorithm.SHA1
//        val DefaultAuthType = AuthType.TOTP
//        val DefaultSource = Source.Manual
//
////        fun createDefault() = Service(
////            id = 0,
////            name = "",
////            secret = "",
//////            iconCollectionId = ServiceIcons.defaultCollectionId,
////            serviceTypeId = null,
////            source = Source.Manual,
////        )
//    }

//    data class Otp(
//        val link: String? = null,
//        val label: String = "",
//        val account: String = "",
//        val issuer: String? = null,
//        val digits: Int = DefaultDigits,
//        val period: Int = DefaultPeriod,
//        val hotpCounter: Int? = null,
//        val algorithm: Algorithm = DefaultAlgorithm,
//    )


}
