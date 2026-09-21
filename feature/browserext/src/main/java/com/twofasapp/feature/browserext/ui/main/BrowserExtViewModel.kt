package com.twofasapp.feature.browserext.ui.main

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.common.ktx.runSafely
import com.twofasapp.data.browserext.BrowserExtRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class BrowserExtViewModel(
    private val browserExtRepository: BrowserExtRepository,
) : ViewModel() {

    val uiState = MutableStateFlow(BrowserExtUiState())

    init {
        launchScoped {
            browserExtRepository.observePairedBrowsers().collect { browsers ->
                uiState.update { state ->
                    state.copy(
                        pairedBrowsers = browsers,
                        // Reveal cached browsers immediately, but keep the loader while the list is
                        // still empty so we never flash the Empty screen before the fetch resolves.
                        loading = if (browsers.isNotEmpty()) false else state.loading,
                    )
                }
            }
        }

        launchScoped {
            browserExtRepository.observeMobileDevice().collect {
                uiState.update { state -> state.copy(mobileDevice = it) }
            }
        }

        launchScoped {
            runSafely { browserExtRepository.fetchPairedBrowsers() }
            uiState.update { it.copy(loading = false) }
        }
    }

    fun updateDeviceName(name: String) {
        launchScoped {
            runSafely {
                browserExtRepository.updateMobileDevice(
                    browserExtRepository.getMobileDevice().copy(name = name),
                )
            }.onFailure {
                uiState.update { state -> state.copy(events = state.events.plus(BrowserExtUiEvent.ShowErrorSnackbar)) }
            }
        }
    }

    fun forgetBrowser(id: String) {
        launchScoped {
            uiState.update { state -> state.copy(deletingBrowserIds = state.deletingBrowserIds.plus(id)) }

            runSafely {
                browserExtRepository.deletePairedBrowser(
                    deviceId = browserExtRepository.getMobileDevice().id,
                    extensionId = id,
                )
            }.onFailure {
                uiState.update { state -> state.copy(events = state.events.plus(BrowserExtUiEvent.ShowErrorSnackbar)) }
            }

            uiState.update { state -> state.copy(deletingBrowserIds = state.deletingBrowserIds.minus(id)) }
        }
    }

    fun consumeEvent(event: BrowserExtUiEvent) {
        uiState.update { state -> state.copy(events = state.events.minus(event)) }
    }
}