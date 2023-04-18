package com.twofasapp.browserextension.domain

import com.twofasapp.data.browserext.BrowserExtRepository

class FetchPairedBrowsersCase(
    private val browserExtensionRepository: BrowserExtRepository
) {

    suspend operator fun invoke() {
        return browserExtensionRepository.fetchPairedBrowsers()
    }
}