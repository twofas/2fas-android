package com.twofasapp.browserextension.domain

import com.twofasapp.data.browserext.BrowserExtRepository
import com.twofasapp.data.browserext.domain.TokenRequest

class FetchTokenRequestsCase(
    private val browserExtensionRepository: BrowserExtRepository
) {

    suspend operator fun invoke(deviceId: String): List<TokenRequest> {
        if (deviceId.isBlank()) return emptyList()

        return browserExtensionRepository.fetchTokenRequests(deviceId)
    }
}