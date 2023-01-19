package com.twofasapp.prefs.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceDto(
    val id: Long = 0,
    val name: String,
    val secret: String,
    val authType: AuthType,
    val otpLink: String? = null,
    val otpLabel: String? = null,
    val otpAccount: String? = null,
    val otpIssuer: String? = null,
    private val otpDigits: Int? = null,
    private val otpPeriod: Int? = null,
    private val otpAlgorithm: String? = null,
    val hotpCounter: Int? = null,
    val backupSyncStatus: BackupSyncStatus = BackupSyncStatus.NOT_SYNCED,
    val updatedAt: Long = 0,
    val badge: Badge? = null,
    val selectedImageType: ImageType = ImageType.IconCollection,
    val labelText: String? = null,
    val labelBackgroundColor: Tint? = null,
    val iconCollectionId: String,
    val groupId: String? = null,
    val isDeleted: Boolean? = null,
    val assignedDomains: List<String>,
    val serviceTypeId: String?,
    val source: Source,
) : Parcelable {

    fun getPeriod() = otpPeriod ?: 30
    fun getDigits() = otpDigits ?: 6
    fun getAlgorithm() = otpAlgorithm ?: "SHA1"

    @Parcelize
    data class Badge(
        val color: Tint,
    ) : Parcelable

    enum class ImageType { IconCollection, Label }

    @Parcelize
    data class Brand(
        val id: ServiceType,
    ) : Parcelable

    companion object {
        fun fromRemote(remoteService: RemoteService, serviceTypeIdFromLegacy: String?, iconCollectionIdFromLegacy: String?): ServiceDto {
            return ServiceDto(
                name = remoteService.name,
                secret = remoteService.secret,
                authType = remoteService.otp.tokenType?.let { AuthType.valueOf(it) } ?: AuthType.TOTP,
                otpLabel = remoteService.otp.label,
                otpAccount = remoteService.otp.account,
                otpIssuer = remoteService.otp.issuer,
                otpDigits = remoteService.otp.digits,
                otpPeriod = remoteService.otp.period,
                otpAlgorithm = remoteService.otp.algorithm,
                hotpCounter = remoteService.otp.counter,
                backupSyncStatus = BackupSyncStatus.SYNCED,
                updatedAt = remoteService.updatedAt,
                badge = remoteService.badge?.color?.let {
                    Badge(it)
                },
                selectedImageType = when (remoteService.icon?.selected) {
                    RemoteService.IconType.Brand -> ImageType.IconCollection
                    RemoteService.IconType.Label -> ImageType.Label
                    RemoteService.IconType.IconCollection -> ImageType.IconCollection
                    else -> ImageType.IconCollection
                },
                labelText = remoteService.icon?.label?.text,
                labelBackgroundColor = remoteService.icon?.label?.backgroundColor,
                iconCollectionId = remoteService.icon?.iconCollection?.id ?: iconCollectionIdFromLegacy
                ?: "a5b3fb65-4ec5-43e6-8ec1-49e24ca9e7ad", // TODO: Icons
                groupId = remoteService.groupId,
                assignedDomains = emptyList(),
                serviceTypeId = remoteService.serviceTypeId ?: serviceTypeIdFromLegacy,
                source = when (remoteService.otp.source.orEmpty().lowercase()) {
                    "manual" -> Source.Manual
                    "link" -> Source.Link
                    else -> if (remoteService.type == ServiceType.ManuallyAdded) Source.Manual else Source.Link
                },
            )
        }
    }

    fun mapToRemote(): RemoteService {
        return RemoteService(
            name = name,
            secret = secret,
            updatedAt = updatedAt,
            serviceTypeId = serviceTypeId,
            otp = RemoteService.Otp(
                link = otpLink,
                label = otpLabel,
                account = otpAccount,
                issuer = otpIssuer,
                digits = otpDigits,
                period = otpPeriod,
                algorithm = otpAlgorithm,
                counter = if (authType == ServiceDto.AuthType.HOTP) hotpCounter else null,
                tokenType = authType.name,
                source = source.name,
            ),
            order = RemoteService.Order(0),
            badge = badge?.color?.let {
                RemoteService.Badge(it)
            },
            icon = RemoteService.Icon(
                selected = when (selectedImageType) {
                    ImageType.IconCollection -> RemoteService.IconType.IconCollection
                    ImageType.Label -> RemoteService.IconType.Label
                },
                brand = null,
                label = labelText?.let { RemoteService.Label(text = labelText, backgroundColor = labelBackgroundColor ?: Tint.LightBlue) },
                iconCollection = RemoteService.IconCollection(id = iconCollectionId)
            ),
            groupId = groupId,
        )
    }

    enum class AuthType { TOTP, HOTP }
    enum class Source { Link, Manual }
}