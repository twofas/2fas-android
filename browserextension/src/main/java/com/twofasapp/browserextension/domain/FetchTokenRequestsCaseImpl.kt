package com.twofasapp.browserextension.domain

import com.twofasapp.browserextension.domain.model.TokenRequest
import com.twofasapp.browserextension.domain.repository.BrowserExtensionRepository

internal class FetchTokenRequestsCaseImpl(
    private val browserExtensionRepository: BrowserExtensionRepository
) : FetchTokenRequestsCase {

    override suspend operator fun invoke(deviceId: String): List<TokenRequest> {
        if(deviceId.isBlank()) return emptyList()

        return browserExtensionRepository.fetchTokenRequests(deviceId)
    }
}