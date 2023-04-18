package com.twofasapp.feature.appsettings.ui

import androidx.lifecycle.ViewModel
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.data.session.domain.SelectedTheme
import com.twofasapp.data.session.domain.ServicesStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class AppSettingsViewModel(
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
            val shouldRecreate = selectedTheme != uiState.value.appSettings.selectedTheme

            settingsRepository.setSelectedTheme(selectedTheme)

            if (shouldRecreate) {
                uiState.update {
                    it.copy(events = it.events.plus(AppSettingsUiEvent.Recreate))
                }
            }
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

    fun toggleShowBackupNotice() {
        launchScoped {
            settingsRepository.setShowBackupNotice(uiState.value.appSettings.showBackupNotice.not())
        }
    }

    fun toggleSendCrashLogs() {
        launchScoped {
            settingsRepository.setSendCrashLogs(uiState.value.appSettings.sendCrashLogs.not())
        }
    }

    fun consumeEvent(event: AppSettingsUiEvent) {
        uiState.update { it.copy(events = it.events.minus(event)) }
    }
}