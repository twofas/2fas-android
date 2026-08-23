package com.twofasapp.feature.browserext.ui.details

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.common.ktx.runSafely
import com.twofasapp.data.browserext.BrowserExtRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class BrowserExtDetailsViewModel(
    private val extensionId: String,
    private val browserExtRepository: BrowserExtRepository,
) : ViewModel() {

    val uiState = MutableStateFlow(BrowserExtDetailsUiState())

    init {
        launchScoped {
            runSafely { browserExtRepository.getPairedBrowser(extensionId) }
                .onSuccess { browser ->
                    uiState.update {
                        it.copy(
                            browserName = browser.name,
                            browserPairedAt = browser.pairedAt,
                        )
                    }
                }
                .onFailure { uiState.update { it.copy(finish = true) } }
        }
    }

    fun forgetBrowser() {
        launchScoped {
            runSafely {
                browserExtRepository.deletePairedBrowser(
                    deviceId = browserExtRepository.getMobileDevice().id,
                    extensionId = extensionId,
                )
            }
                .onSuccess { uiState.update { it.copy(finish = true) } }
                .onFailure { }
        }
    }
}