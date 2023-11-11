package com.twofasapp.data.services.mapper

import com.twofasapp.common.domain.BackupSyncStatus
import com.twofasapp.common.domain.Service
import com.twofasapp.common.ktx.enumValueOrNull
import com.twofasapp.data.services.domain.BackupService
import com.twofasapp.data.services.domain.ServicesOrder
import com.twofasapp.data.services.local.model.ServiceEntity
import com.twofasapp.data.services.local.model.ServicesOrderEntity
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.parsers.SupportedServices
import com.twofasapp.prefs.model.Tint

internal fun ServiceEntity.asDomain(): Service {
    val iconCollectionId = iconCollectionId ?: ServiceIcons.defaultCollectionId
    val iconLight = ServiceIcons.getIcon(iconCollectionId, false)
    val iconDark = ServiceIcons.getIcon(iconCollectionId, true)

    return Service(
        id = id,
        serviceTypeId = serviceTypeId,
        secret = secret,
        name = name,
        info = otpAccount,
        authType = authType?.let { Service.AuthType.valueOf(it) } ?: Service.DefaultAuthType,
        period = otpPeriod,
        digits = otpDigits,
        algorithm = otpAlgorithm?.let { Service.Algorithm.valueOf(it) },
        groupId = groupId,
        imageType = selectedImageType?.let {
            when (it) {
                "Brand" -> Service.ImageType.IconCollection
                "Label" -> Service.ImageType.Label
                else -> Service.ImageType.IconCollection
            }
        } ?: Service.ImageType.IconCollection,
        iconCollectionId = iconCollectionId,
        iconLight = iconLight,
        iconDark = iconDark,
        labelText = labelText,
        labelColor = labelBackgroundColor?.let { color -> Service.Tint.valueOf(color) },
        badgeColor = badgeColor?.let { Service.Tint.valueOf(it) },
        tags = SupportedServices.list.firstOrNull { serviceTypeId == it.id }?.tags?.map { it.lowercase() }.orEmpty(),
        code = null,
        link = otpLink,
        issuer = otpIssuer,
        hotpCounter = hotpCounter,
        hotpCounterTimestamp = hotpCounterTimestamp,
        isDeleted = isDeleted ?: false,
        updatedAt = updatedAt,
        source = source?.let { Service.Source.valueOf(it) } ?: Service.Source.Manual,
        assignedDomains = assignedDomains.orEmpty(),
        backupSyncStatus = BackupSyncStatus.valueOf(backupSyncStatus),
        revealTimestamp = revealTimestamp,
    )
}

internal fun Service.asEntity(): ServiceEntity {
    return ServiceEntity(
        id = id,
        name = name,
        secret = secret.trim(),
        serviceTypeId = serviceTypeId,
        iconCollectionId = iconCollectionId,
        source = source.name,
        otpLink = link,
        otpLabel = info,
        otpAccount = info,
        otpIssuer = issuer,
        otpDigits = digits,
        otpPeriod = period,
        otpAlgorithm = algorithm?.name,
        backupSyncStatus = backupSyncStatus.name,
        updatedAt = updatedAt,
        badgeColor = badgeColor?.name,
        selectedImageType = imageType.name,
        labelText = labelText,
        labelBackgroundColor = labelColor?.name,
        groupId = groupId,
        isDeleted = isDeleted,
        authType = authType.name,
        hotpCounter = hotpCounter,
        hotpCounterTimestamp = hotpCounterTimestamp,
        assignedDomains = assignedDomains,
        revealTimestamp = revealTimestamp,
    )
}

internal fun List<Service>.asBackup(): List<BackupService> {
    return mapIndexedNotNull { index, s ->
        BackupService(
            name = s.name,
            secret = s.secret,
            updatedAt = s.updatedAt,
            serviceTypeId = s.serviceTypeId,
            otp = BackupService.Otp(
                link = s.link,
                label = s.info,
                account = s.info,
                issuer = s.issuer,
                digits = s.digits,
                period = s.period,
                algorithm = s.algorithm?.name,
                counter = if (s.authType == Service.AuthType.HOTP) s.hotpCounter else null,
                tokenType = s.authType.name,
                source = s.source.name,
            ),
            order = BackupService.Order(position = index),
            badge = s.badgeColor?.let { BackupService.Badge(it.name) },
            icon = BackupService.Icon(
                selected = when (s.imageType) {
                    Service.ImageType.IconCollection -> BackupService.IconType.IconCollection
                    Service.ImageType.Label -> BackupService.IconType.Label
                },
                brand = null,
                label = s.labelText?.let { BackupService.Label(text = it, backgroundColor = s.labelColor?.name ?: Tint.LightBlue.name) },
                iconCollection = BackupService.IconCollection(id = s.iconCollectionId)
            ),
            groupId = s.groupId
        )
    }
}

internal fun BackupService.asDomain(
    serviceTypeIdFromLegacy: String?,
    iconCollectionIdFromLegacy: String?,
): Service {
    val iconCollectionId = icon?.iconCollection?.id ?: iconCollectionIdFromLegacy ?: ServiceIcons.defaultCollectionId

    return Service(
        id = 0L,
        serviceTypeId = serviceTypeId ?: serviceTypeIdFromLegacy,
        secret = secret,
        name = name,
        info = otp.account ?: otp.label,
        authType = otp.tokenType?.let { enumValueOrNull<Service.AuthType>(it) } ?: Service.AuthType.TOTP,
        link = otp.link,
        issuer = otp.issuer,
        period = otp.period,
        digits = otp.digits,
        hotpCounter = otp.counter,
        hotpCounterTimestamp = null,
        algorithm = otp.algorithm?.let { enumValueOrNull<Service.Algorithm>(it) },
        groupId = groupId,
        imageType = when (icon?.selected) {
            BackupService.IconType.Brand -> Service.ImageType.IconCollection
            BackupService.IconType.Label -> Service.ImageType.Label
            BackupService.IconType.IconCollection -> Service.ImageType.IconCollection
            else -> Service.ImageType.IconCollection
        },
        iconCollectionId = iconCollectionId,
        iconLight = ServiceIcons.getIcon(iconCollectionId, false),
        iconDark = ServiceIcons.getIcon(iconCollectionId, true),
        labelText = icon?.label?.text,
        labelColor = icon?.label?.backgroundColor?.let { enumValueOrNull<Service.Tint>(it) },
        badgeColor = badge?.color?.let { enumValueOrNull<Service.Tint>(it) },
        isDeleted = false,
        updatedAt = updatedAt,
        source = when (otp.source?.lowercase()) {
            "manual" -> Service.Source.Manual
            "link" -> Service.Source.Link
            else -> Service.Source.Manual
        },
        assignedDomains = listOf(),
        backupSyncStatus = BackupSyncStatus.NOT_SYNCED,
        tags = emptyList(),
        revealTimestamp = null,
    )
}

internal fun ServicesOrderEntity.asDomain() =
    ServicesOrder(
        ids = ids,
        type = when (type) {
            ServicesOrderEntity.Type.Alphabetical -> ServicesOrder.Type.Alphabetical
            ServicesOrderEntity.Type.Manual -> ServicesOrder.Type.Manual
        }
    )