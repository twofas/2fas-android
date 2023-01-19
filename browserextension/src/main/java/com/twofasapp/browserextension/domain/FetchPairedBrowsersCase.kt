package com.twofasapp.browserextension.domain

import com.twofasapp.browserextension.domain.repository.BrowserExtensionRepository

internal class FetchPairedBrowsersCase(
    private val browserExtensionRepository: BrowserExtensionRepository
) {

    suspend operator fun invoke() {
        return browserExtensionRepository.fetchPairedBrowsers()
    }
}