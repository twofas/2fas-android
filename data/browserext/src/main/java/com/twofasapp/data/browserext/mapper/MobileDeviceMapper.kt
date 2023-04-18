package com.twofasapp.data.browserext.mapper

import com.twofasapp.data.browserext.domain.MobileDevice
import com.twofasapp.data.browserext.local.model.MobileDeviceEntity

internal fun MobileDevice.asEntity() = MobileDeviceEntity(
    id = id,
    name = name,
    fcmToken = fcmToken,
    platform = platform,
    publicKey = publicKey,
)

internal fun MobileDeviceEntity.asDomain() = MobileDevice(
    id = id,
    name = name,
    fcmToken = fcmToken,
    platform = platform,
    publicKey = publicKey,
)
