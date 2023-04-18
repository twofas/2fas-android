package com.twofasapp.browserextension.domain

import com.twofasapp.data.browserext.BrowserExtRepository
import kotlinx.coroutines.flow.first

class DeletePairedBrowserCase(
    private val observeMobileDeviceCase: ObserveMobileDeviceCase,
    private val browserExtensionRepository: BrowserExtRepository,
) {
    suspend operator fun invoke(extensionId: String) {
        return browserExtensionRepository.deletePairedBrowser(
            deviceId = observeMobileDeviceCase().first().id,
            extensionId = extensionId,
        )
    }
}
