package com.twofasapp.browserextension.domain

import com.twofasapp.data.browserext.BrowserExtRepository
import kotlinx.coroutines.flow.first

class UpdateMobileDeviceCase(
    private val browserExtensionRepository: BrowserExtRepository
) {

    data class Params(
        val newDeviceName: String,
    )

    suspend operator fun invoke(newDeviceName: String) {
        return browserExtensionRepository.updateMobileDevice(
            browserExtensionRepository.observeMobileDevice().first().copy(name = newDeviceName)
        )
    }
}