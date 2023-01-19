package com.twofasapp.browserextension.ui.browser

import androidx.lifecycle.viewModelScope
import com.twofasapp.base.BaseViewModel
import com.twofasapp.base.dispatcher.Dispatchers
import com.twofasapp.browserextension.domain.DeletePairedBrowserCase
import com.twofasapp.browserextension.domain.ObservePairedBrowsersCase
import com.twofasapp.navigation.SettingsDirections
import com.twofasapp.navigation.SettingsRouter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class BrowserDetailsViewModel(
    private val dispatchers: Dispatchers,
    private val observePairedBrowsersCase: ObservePairedBrowsersCase,
    private val deletePairedBrowserCase: DeletePairedBrowserCase,
    private val settingsRouter: SettingsRouter,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(BrowserDetailsUiState())
    val uiState = _uiState.asStateFlow()

    fun init(extensionId: String) {
        viewModelScope.launch {
            observePairedBrowsersCase().flowOn(dispatchers.io()).collect { list ->
                val browser = list.find { it.id == extensionId }

                if (browser != null) {
                    _uiState.update {
                        it.copy(
                            extensionId = extensionId,
                            browserName = browser.name,
                            browserPairedAt = browser.formatPairedAt(),
                        )
                    }
                }
            }
        }
    }

    fun showConfirmForget() {
        _uiState.update { it.copy(showConfirmDeleteDialog = true) }

    }

    fun dismissConfirmForget() {
        _uiState.update { it.copy(showConfirmDeleteDialog = false) }
    }

    fun forgetBrowser() {
        viewModelScope.launch(dispatchers.io()) {
            runSafely(catch = { postError() }) {
                deletePairedBrowserCase(uiState.value.extensionId)
                settingsRouter.navigate(SettingsDirections.GoBack)
            }
        }
    }

    fun eventHandled(id: String) {
        _uiState.update { it.reduceEvent(id) }
    }

    private fun postError() {
        _uiState.update {
            it.postEvent(BrowserDetailsUiState.Event.ShowSnackbarError("Network error. Please try again."))
        }
    }
}
