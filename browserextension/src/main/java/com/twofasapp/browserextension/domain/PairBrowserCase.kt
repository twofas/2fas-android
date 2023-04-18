package com.twofasapp.browserextension.domain

import com.twofasapp.data.browserext.BrowserExtRepository
import kotlinx.coroutines.flow.first

class PairBrowserCase(
    private val browserExtensionRepository: BrowserExtRepository,
    private val observeMobileDeviceCase: ObserveMobileDeviceCase,
) {

    data class Params(
        val extensionId: String
    )

    suspend operator fun invoke(extensionId: String) {
        val mobileDevice = observeMobileDeviceCase().first()

        browserExtensionRepository.pairBrowser(
            deviceId = mobileDevice.id,
            extensionId = extensionId,
            deviceName = mobileDevice.name,
            devicePublicKey = mobileDevice.publicKey,
        )
    }
}
