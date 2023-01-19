package com.twofasapp.browserextension.domain

import com.twofasapp.browserextension.domain.model.PairedBrowser
import kotlinx.coroutines.flow.Flow

interface ObservePairedBrowsersCase {
    operator fun invoke(): Flow<List<PairedBrowser>>
}