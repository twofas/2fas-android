package com.twofasapp.feature.browserext.ui.main

import com.twofasapp.data.browserext.domain.MobileDevice
import com.twofasapp.data.browserext.domain.PairedBrowser

internal data class BrowserExtUiState(
    val loading: Boolean = true,
    val pairedBrowsers: List<PairedBrowser> = emptyList(),
    val mobileDevice: MobileDevice = MobileDevice.Empty,
    val events: List<BrowserExtUiEvent> = emptyList(),
)

internal sealed interface BrowserExtUiEvent {
    data object ShowErrorSnackbar : BrowserExtUiEvent
}