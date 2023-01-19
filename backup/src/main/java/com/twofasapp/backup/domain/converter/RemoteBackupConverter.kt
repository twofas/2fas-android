package com.twofasapp.backup.domain.converter

import com.twofasapp.prefs.model.Group
import com.twofasapp.prefs.model.RemoteGroup
import com.twofasapp.prefs.model.RemoteService
import com.twofasapp.prefs.model.Tint
import com.twofasapp.services.domain.model.Service

fun Service.toRemoteService() = RemoteService(
    name = name,
    secret = secret,
    updatedAt = updatedAt,
    serviceTypeId = serviceTypeId,
    otp = RemoteService.Otp(
        link = otp.link,
        label = otp.label,
        account = otp.account,
        issuer = otp.issuer,
        digits = otp.digits,
        period = otp.period,
        algorithm = otp.algorithm.name,
        counter = if (authType == Service.AuthType.HOTP) otp.hotpCounter else null,
        tokenType = authType.name,
        source = source.name,
    ),
    order = RemoteService.Order(0),
    badge = badge?.color?.let {
        RemoteService.Badge(it)
    },
    icon = RemoteService.Icon(
        selected = when (selectedImageType) {
            Service.ImageType.IconCollection -> RemoteService.IconType.IconCollection
            Service.ImageType.Label -> RemoteService.IconType.Label
        },
        brand = null,
        label = labelText?.let { RemoteService.Label(text = it, backgroundColor = labelBackgroundColor ?: Tint.LightBlue) },
        iconCollection = RemoteService.IconCollection(id = iconCollectionId)
    ),
    groupId = groupId,
)


fun Group.toRemoteGroup() = RemoteGroup(
    id = id.orEmpty(),
    name = name,
    isExpanded = isExpanded,
    updatedAt = updatedAt
)