package com.twofasapp.browserextension.domain

import com.twofasapp.data.browserext.BrowserExtRepository
import com.twofasapp.data.browserext.domain.PairedBrowser
import kotlinx.coroutines.flow.Flow

class ObservePairedBrowsersCase(
    private val browserExtensionRepository: BrowserExtRepository
) {

    operator fun invoke(): Flow<List<PairedBrowser>> {
        return browserExtensionRepository.observePairedBrowsers()
    }
}