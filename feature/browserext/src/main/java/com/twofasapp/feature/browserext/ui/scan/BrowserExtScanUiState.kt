package com.twofasapp.feature.browserext.ui.scan

internal data class BrowserExtScanUiState(
    val events: List<BrowserExtScanUiEvent> = emptyList(),
)

internal sealed interface BrowserExtScanUiEvent {
    data object ShowUnsupportedFormatError : BrowserExtScanUiEvent
    data object ShowUnknownError : BrowserExtScanUiEvent
    data class Success(val extensionId: String) : BrowserExtScanUiEvent
}