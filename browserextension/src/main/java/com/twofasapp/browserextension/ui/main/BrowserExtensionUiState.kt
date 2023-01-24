package com.twofasapp.browserextension.ui.main

import com.twofasapp.base.UiEvent
import com.twofasapp.base.UiState
import com.twofasapp.data.browserext.domain.MobileDevice
import com.twofasapp.data.browserext.domain.PairedBrowser

data class BrowserExtensionUiState(
    val isLoading: Boolean = true,
    val pairedBrowsers: List<PairedBrowser> = emptyList(),
    val mobileDevice: MobileDevice? = null,
    val isCameraPermissionGranted: Boolean = false,
    val showRationaleDialog: Boolean = false,
    val showEditDeviceDialog: Boolean = false,
    override val events: List<Event> = emptyList(),
) : UiState<BrowserExtensionUiState, BrowserExtensionUiState.Event> {

    sealed class Event : UiEvent() {
        class ShowSnackbarError(val message: String) : Event()
    }

    override fun copyStateWithNewEvents(events: List<Event>): BrowserExtensionUiState {
        return copy(events = events)
    }
}
