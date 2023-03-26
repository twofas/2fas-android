package com.twofasapp.services.data.converter

import com.twofasapp.di.BackupSyncStatus
import com.twofasapp.extensions.removeWhiteCharacters
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.prefs.model.ServiceDto
import com.twofasapp.prefs.model.Tint
import com.twofasapp.services.domain.model.Service

internal fun com.twofasapp.data.services.local.model.ServiceEntity.toService() = Service(
    id = id,
    name = name,
    secret = secret,
    authType = authType?.let { Service.AuthType.valueOf(it) } ?: Service.DefaultAuthType,
    otp = Service.Otp(
        link = otpLink,
        label = otpLabel.orEmpty(),
        account = otpAccount.orEmpty(),
        issuer = otpIssuer,
        digits = otpDigits ?: Service.DefaultDigits,
        period = otpPeriod ?: Service.DefaultPeriod,
        hotpCounter = hotpCounter,
        algorithm = otpAlgorithm?.let { Service.Algorithm.valueOf(it) } ?: Service.Algorithm.SHA1
    ),
    badge = badgeColor?.let { Service.Badge(Tint.valueOf(it)) },
    selectedImageType = selectedImageType?.let {
        when (it) {
            "Brand" -> Service.ImageType.IconCollection
            "Label" -> Service.ImageType.Label
            else -> Service.ImageType.IconCollection
        }
    } ?: Service.ImageType.IconCollection,
    labelText = labelText,
    labelBackgroundColor = labelBackgroundColor?.let { color -> Tint.valueOf(color) },
    iconCollectionId = iconCollectionId ?: ServiceIcons.defaultCollectionId,
    groupId = groupId,
    assignedDomains = assignedDomains.orEmpty(),
    isDeleted = isDeleted ?: false,
    backupSyncStatus = BackupSyncStatus.valueOf(backupSyncStatus),
    updatedAt = updatedAt,
    serviceTypeId = serviceTypeId,
    source = source?.let { Service.Source.valueOf(it) } ?: Service.DefaultSource,
)

internal fun Service.toEntity() = com.twofasapp.data.services.local.model.ServiceEntity(
    id = id,
    name = name,
    secret = secret.removeWhiteCharacters(),
    serviceTypeId = serviceTypeId,
    iconCollectionId = iconCollectionId,
    source = source.name,
    otpLink = otp.link,
    otpLabel = otp.label,
    otpAccount = otp.account,
    otpIssuer = otp.issuer,
    otpDigits = otp.digits,
    otpPeriod = otp.period,
    otpAlgorithm = otp.algorithm.name,
    backupSyncStatus = backupSyncStatus.name,
    updatedAt = updatedAt,
    badgeColor = badge?.color?.name,
    selectedImageType = selectedImageType.name,
    labelText = labelText,
    labelBackgroundColor = labelBackgroundColor?.name,
    groupId = groupId,
    isDeleted = isDeleted,
    authType = authType.name,
    hotpCounter = otp.hotpCounter,
    hotpCounterTimestamp = null,
    assignedDomains = assignedDomains,
)

internal fun Service.toDeprecatedDto() = ServiceDto(
    id = id,
    name = name,
    secret = secret.removeWhiteCharacters(),
    authType = ServiceDto.AuthType.valueOf(authType.name),
    otpLink = otp.link,
    otpLabel = otp.label,
    otpAccount = otp.account,
    otpIssuer = otp.issuer,
    otpDigits = otp.digits,
    otpPeriod = otp.period,
    otpAlgorithm = otp.algorithm.name,
    backupSyncStatus = backupSyncStatus,
    updatedAt = updatedAt,
    badge = badge?.color?.let { ServiceDto.Badge(it) },
    selectedImageType = when (selectedImageType) {
        Service.ImageType.IconCollection -> ServiceDto.ImageType.IconCollection
        Service.ImageType.Label -> ServiceDto.ImageType.Label
    },
    labelText = labelText,
    labelBackgroundColor = labelBackgroundColor,
    iconCollectionId = iconCollectionId,
    groupId = groupId,
    isDeleted = isDeleted,
    hotpCounter = otp.hotpCounter,
    assignedDomains = assignedDomains,
    serviceTypeId = serviceTypeId,
    source = when (source) {
        Service.Source.Link -> ServiceDto.Source.Link
        Service.Source.Manual -> ServiceDto.Source.Manual
    }
)

fun ServiceDto.toService() = Service(
    id = id,
    name = name,
    secret = secret,
    authType = Service.AuthType.valueOf(authType.name),
    otp = Service.Otp(
        link = otpLink,
        label = otpLabel.orEmpty(),
        account = otpAccount.orEmpty(),
        issuer = otpIssuer,
        digits = getDigits(),
        period = getPeriod(),
        hotpCounter = hotpCounter,
        algorithm = getAlgorithm().let { Service.Algorithm.valueOf(it) }
    ),
    backupSyncStatus = backupSyncStatus,
    updatedAt = updatedAt,
    badge = badge?.color?.let { Service.Badge(it) },
    selectedImageType = when (selectedImageType) {
        ServiceDto.ImageType.IconCollection -> Service.ImageType.IconCollection
        ServiceDto.ImageType.Label -> Service.ImageType.Label
    },
    labelText = labelText,
    labelBackgroundColor = labelBackgroundColor,
    iconCollectionId = iconCollectionId,
    groupId = groupId,
    isDeleted = isDeleted ?: false,
    assignedDomains = assignedDomains,
    serviceTypeId = serviceTypeId,
    source = when (source) {
        ServiceDto.Source.Link -> Service.Source.Link
        ServiceDto.Source.Manual -> Service.Source.Manual
    }
)
