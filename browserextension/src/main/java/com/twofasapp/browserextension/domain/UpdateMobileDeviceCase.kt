package com.twofasapp.browserextension.domain

import com.twofasapp.browserextension.domain.repository.BrowserExtensionRepository
import kotlinx.coroutines.flow.first

internal class UpdateMobileDeviceCase(
    private val browserExtensionRepository: BrowserExtensionRepository
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