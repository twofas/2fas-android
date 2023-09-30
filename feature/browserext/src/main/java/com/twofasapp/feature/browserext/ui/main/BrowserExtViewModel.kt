package com.twofasapp.feature.browserext.ui.main

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.common.ktx.runSafely
import com.twofasapp.data.browserext.BrowserExtRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

internal class BrowserExtViewModel(
    private val browserExtRepository: BrowserExtRepository,
) : ViewModel() {

    val uiState = MutableStateFlow(BrowserExtUiState())

    init {
        launchScoped {
            browserExtRepository.observePairedBrowsers().collect {
                uiState.update { state -> state.copy(pairedBrowsers = it, loading = false) }
            }
        }

        launchScoped {
            browserExtRepository.observeMobileDevice().collect {
                uiState.update { state -> state.copy(mobileDevice = it, loading = false) }
            }
        }

        launchScoped {
            runSafely { browserExtRepository.fetchPairedBrowsers() }
                .onSuccess { uiState.update { it.copy(loading = false) } }
        }
    }

    fun updateDeviceName(name: String) {
        launchScoped {
            runSafely {
                browserExtRepository.updateMobileDevice(
                    browserExtRepository.getMobileDevice().copy(name = name)
                )
            }.onFailure {
                uiState.update { state -> state.copy(events = state.events.plus(BrowserExtUiEvent.ShowErrorSnackbar)) }
            }
        }
    }

    fun consumeEvent(event: BrowserExtUiEvent) {
        uiState.update { state -> state.copy(events = state.events.minus(event)) }
    }
}