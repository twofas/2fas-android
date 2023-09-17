package com.twofasapp.browserextension.ui.main

import androidx.lifecycle.viewModelScope
import com.twofasapp.base.BaseViewModel
import com.twofasapp.browserextension.domain.FetchPairedBrowsersCase
import com.twofasapp.browserextension.domain.ObserveMobileDeviceCase
import com.twofasapp.browserextension.domain.ObservePairedBrowsersCase
import com.twofasapp.browserextension.domain.UpdateMobileDeviceCase
import com.twofasapp.common.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BrowserExtensionViewModel(
    private val dispatchers: Dispatchers,
    private val observeMobileDeviceCase: ObserveMobileDeviceCase,
    private val observePairedBrowsersCase: ObservePairedBrowsersCase,
    private val updateMobileDeviceCase: UpdateMobileDeviceCase,
    private val fetchPairedBrowsersCase: FetchPairedBrowsersCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(BrowserExtensionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {

            launch {
                combine(
                    observeMobileDeviceCase().flowOn(dispatchers.io),
                    observePairedBrowsersCase().flowOn(dispatchers.io),
                ) { a, b -> Pair(a, b) }.collect { (mobileDevice, pairedBrowsers) ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            pairedBrowsers = pairedBrowsers,
                            mobileDevice = mobileDevice,
                        )
                    }
                }
            }

            launch(dispatchers.io) {
                runCatching { fetchPairedBrowsersCase() }
            }
        }
    }

    fun onEditDeviceClick() {
        _uiState.update { it.copy(showEditDeviceDialog = true) }
    }

    fun onEditDeviceDialogDismiss(newName: String? = null) {
        if (newName != null) {
            viewModelScope.launch(dispatchers.io) {
                try {
                    updateMobileDeviceCase(newName)
                } catch (e: Exception) {
                    _uiState.update {
                        it.postEvent(BrowserExtensionUiState.Event.ShowSnackbarError("Network error. Please try again."))
                    }
                }
            }
        }

        _uiState.update { it.copy(showEditDeviceDialog = false) }
    }

    fun eventHandled(id: String) {
        _uiState.update { it.reduceEvent(id) }
    }
}
