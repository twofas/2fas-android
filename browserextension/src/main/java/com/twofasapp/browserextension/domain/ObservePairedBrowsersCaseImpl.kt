package com.twofasapp.browserextension.domain

import com.twofasapp.browserextension.domain.model.PairedBrowser
import com.twofasapp.browserextension.domain.repository.BrowserExtensionRepository
import kotlinx.coroutines.flow.Flow

internal class ObservePairedBrowsersCaseImpl(
    private val browserExtensionRepository: BrowserExtensionRepository
) : ObservePairedBrowsersCase {

    override operator fun invoke(): Flow<List<PairedBrowser>> {
        return browserExtensionRepository.observePairedBrowsers()
    }
}