package com.twofasapp.data.services.mapper

import com.twofasapp.data.services.domain.Service
import com.twofasapp.data.services.domain.ServicesOrder
import com.twofasapp.data.services.local.model.ServiceEntity
import com.twofasapp.data.services.local.model.ServicesOrderEntity
import com.twofasapp.di.BackupSyncStatus
import com.twofasapp.parsers.ServiceIcons
import com.twofasapp.parsers.SupportedServices

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
        authType = authType?.let { Service.AuthType.valueOf(it) } ?: Service.AuthType.TOTP,
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
        backupSyncStatus = BackupSyncStatus.valueOf(backupSyncStatus)
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
        assignedDomains = assignedDomains
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