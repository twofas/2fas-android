package com.twofasapp.browserextension.domain

import com.twofasapp.browserextension.domain.repository.BrowserExtensionRepository
import kotlinx.coroutines.flow.first

internal class DeletePairedBrowserCase(
    private val observeMobileDeviceCase: ObserveMobileDeviceCase,
    private val browserExtensionRepository: BrowserExtensionRepository,
) {
    suspend operator fun invoke(extensionId: String) {
        return browserExtensionRepository.deletePairedBrowser(
            deviceId = observeMobileDeviceCase().first().id,
            extensionId = extensionId,
        )
    }
}
