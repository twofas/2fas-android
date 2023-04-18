package com.twofasapp.browserextension.domain

import com.twofasapp.data.browserext.BrowserExtRepository
import com.twofasapp.data.browserext.domain.MobileDevice
import kotlinx.coroutines.flow.Flow

class ObserveMobileDeviceCase(
    private val browserExtensionRepository: BrowserExtRepository
) {

    operator fun invoke(): Flow<MobileDevice> {
        return browserExtensionRepository.observeMobileDevice()
    }
}