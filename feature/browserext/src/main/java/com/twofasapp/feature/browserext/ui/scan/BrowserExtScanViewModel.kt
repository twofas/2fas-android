package com.twofasapp.feature.browserext.ui.scan

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class BrowserExtScanViewModel : ViewModel() {

    val uiState = MutableStateFlow(BrowserExtScanUiState())

    fun scanned(text: String) {
        try {
            val uri = Uri.parse(text)

            if (uri.scheme.equals("twofas_c")) {
                publishEvent(BrowserExtScanUiEvent.Success(extensionId = text.removePrefix("twofas_c://")))
            } else {
                publishEvent(BrowserExtScanUiEvent.ShowUnsupportedFormatError)
            }
        } catch (e: Exception) {
            publishEvent(BrowserExtScanUiEvent.ShowUnknownError)
        }
    }


    fun consumeEvent(event: BrowserExtScanUiEvent) {
        uiState.update { state -> state.copy(events = state.events.minus(event)) }
    }

    private fun publishEvent(event: BrowserExtScanUiEvent) {
        uiState.update { state -> state.copy(events = state.events.plus(event)) }
    }
}