package com.twofasapp.browserextension.domain

import com.twofasapp.browserextension.domain.model.MobileDevice
import com.twofasapp.browserextension.domain.repository.BrowserExtensionRepository
import kotlinx.coroutines.flow.Flow

internal class ObserveMobileDeviceCaseImpl(
    private val browserExtensionRepository: BrowserExtensionRepository
) : ObserveMobileDeviceCase {

    override operator fun invoke(): Flow<MobileDevice> {
        return browserExtensionRepository.observeMobileDevice()
    }
}