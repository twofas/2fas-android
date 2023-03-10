package com.twofasapp.feature.appsettings.ui

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.data.session.domain.SelectedTheme
import com.twofasapp.data.session.domain.ServicesStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class AppSettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState = MutableStateFlow(AppSettingsUiState())

    init {
        launchScoped {
            settingsRepository.observeAppSettings()
                .collect { appSettings ->
                    uiState.update { it.copy(appSettings = appSettings) }
                }
        }
    }

    fun setSelectedTheme(selectedTheme: SelectedTheme) {
        launchScoped {
            settingsRepository.setSelectedTheme(selectedTheme)
        }
    }

    fun setServiceStyle(servicesStyle: ServicesStyle) {
        launchScoped {
            settingsRepository.setServicesStyle(servicesStyle)
        }
    }

    fun toggleShowNextToken() {
        launchScoped {
            settingsRepository.setShowNextCode(uiState.value.appSettings.showNextCode.not())
        }
    }

    fun toggleAutoFocusSearch() {
        launchScoped {
            settingsRepository.setAutoFocusSearch(uiState.value.appSettings.autoFocusSearch.not())
        }
    }
}