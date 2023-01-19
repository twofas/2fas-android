package com.twofasapp.browserextension.domain

import com.twofasapp.browserextension.domain.repository.BrowserExtensionRepository

internal class DenyLoginRequestCase(
    private val browserExtensionRepository: BrowserExtensionRepository,
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
