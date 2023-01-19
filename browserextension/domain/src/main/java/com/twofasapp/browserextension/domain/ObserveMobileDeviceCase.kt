package com.twofasapp.browserextension.domain

import com.twofasapp.browserextension.domain.model.MobileDevice
import kotlinx.coroutines.flow.Flow

interface ObserveMobileDeviceCase {
    operator fun invoke(): Flow<MobileDevice>
}