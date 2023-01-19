package com.twofasapp.browserextension.domain

import com.twofasapp.browserextension.domain.repository.BrowserExtensionRepository
import kotlinx.coroutines.flow.first

internal class ApproveLoginRequestCase(
    private val browserExtensionRepository: BrowserExtensionRepository,
    private val observeMobileDeviceCase: ObserveMobileDeviceCase,
    private val observePairedBrowsersCase: ObservePairedBrowsersCase,
    private val encryptCodeCase: EncryptCodeCase,
) {
    suspend operator fun invoke(
        extensionId: String,
        requestId: String,
        code: String,
    ) {

        val mobileDevice = observeMobileDeviceCase().first()
        val extension = observePairedBrowsersCase().first().first { it.id == extensionId }
        val codeEncrypted = encryptCodeCase(EncryptCodeCase.Params(code = code, extensionPublicKey = extension.extensionPublicKey))

        return browserExtensionRepository.acceptLoginRequest(
            deviceId = mobileDevice.id,
            extensionId = extensionId,
            requestId = requestId,
            code = codeEncrypted,
        )
    }
}
