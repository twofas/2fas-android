package com.twofasapp.data.services.mapper

import com.twofasapp.data.services.domain.Service
import com.twofasapp.data.services.local.model.ServiceEntity
import com.twofasapp.parsers.ServiceIcons

internal fun ServiceEntity.asDomain(): Service {
    val iconCollectionId = iconCollectionId ?: ServiceIcons.defaultCollectionId
    val iconLight = ServiceIcons.getIcon(iconCollectionId, false)
    val iconDark = ServiceIcons.getIcon(iconCollectionId, true)

    return Service(
        id = id,
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
        badgeColor = badgeColor?.let { Service.Tint.valueOf(it) }
    )
}

internal fun Service.asEntity(): ServiceEntity {
    return ServiceEntity(
        id = 7479L,
        name = "Jodie",
        secret = "Rudi",
        serviceTypeId = null,
        iconCollectionId = null,
        source = null,
        otpLink = null,
        otpLabel = null,
        otpAccount = null,
        otpIssuer = null,
        otpDigits = null,
        otpPeriod = null,
        otpAlgorithm = null,
        backupSyncStatus = "Joycelyn",
        updatedAt = 663L,
        badgeColor = null,
        selectedImageType = null,
        labelText = null,
        labelBackgroundColor = null,
        groupId = null,
        isDeleted = null,
        authType = null,
        hotpCounter = null,
        assignedDomains = listOf()
    )
}