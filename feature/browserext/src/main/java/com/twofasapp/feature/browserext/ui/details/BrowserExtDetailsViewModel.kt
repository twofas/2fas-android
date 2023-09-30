package com.twofasapp.feature.browserext.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.twofasapp.android.navigation.NavArg
import com.twofasapp.android.navigation.getOrThrow
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.common.ktx.runSafely
import com.twofasapp.data.browserext.BrowserExtRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class BrowserExtDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val browserExtRepository: BrowserExtRepository,
) : ViewModel() {

    private val extensionId: String = savedStateHandle.getOrThrow(NavArg.ExtensionId.name)
    val uiState = MutableStateFlow(BrowserExtDetailsUiState())

    init {
        launchScoped {
            val browser = browserExtRepository.getPairedBrowser(extensionId)
            uiState.update {
                it.copy(
                    browserName = browser.name,
                    browserPairedAt = browser.pairedAt,
                )
            }
        }
    }

    fun forgetBrowser() {
        launchScoped {
            runSafely {
                browserExtRepository.deletePairedBrowser(
                    deviceId = browserExtRepository.getMobileDevice().id,
                    extensionId = extensionId
                )
            }
                .onSuccess { uiState.update { it.copy(finish = true) } }
                .onFailure { }
        }
    }
}