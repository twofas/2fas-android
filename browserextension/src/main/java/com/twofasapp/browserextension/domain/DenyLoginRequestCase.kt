package com.twofasapp.browserextension.domain

import com.twofasapp.data.browserext.BrowserExtRepository

class DenyLoginRequestCase(
    private val browserExtensionRepository: BrowserExtRepository,
) {
    suspend operator fun invoke(
        extensionId: String,
        requestId: String,
    ) {
        return browserExtensionRepository.denyLoginRequest(
            extensionId = extensionId,
            requestId = requestId,
        )
    }
}
