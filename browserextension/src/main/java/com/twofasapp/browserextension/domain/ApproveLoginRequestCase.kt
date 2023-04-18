package com.twofasapp.browserextension.domain

import com.twofasapp.data.browserext.BrowserExtRepository
import kotlinx.coroutines.flow.first

class ApproveLoginRequestCase(
    private val browserExtensionRepository: BrowserExtRepository,
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
