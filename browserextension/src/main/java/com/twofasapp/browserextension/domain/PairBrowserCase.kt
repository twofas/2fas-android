package com.twofasapp.browserextension.domain

import com.twofasapp.browserextension.domain.repository.BrowserExtensionRepository
import kotlinx.coroutines.flow.first

internal class PairBrowserCase(
    private val browserExtensionRepository: BrowserExtensionRepository,
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
