package com.twofasapp.feature.appsettings.ui

import androidx.lifecycle.ViewModel
import com.twofasapp.common.domain.SelectedTheme
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.session.CustomizationRepository
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.data.session.domain.ServicesStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class CustomizationViewModel(
    private val customizationRepository: CustomizationRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val uiState = MutableStateFlow(CustomizationUiState())

    init {
        launchScoped {
            customizationRepository.observeSelectedTheme().collect { selectedTheme ->
                uiState.update { it.copy(selectedTheme = selectedTheme) }
            }
        }

        launchScoped {
            customizationRepository.observeDynamicColors().collect { dynamicColors ->
                uiState.update { it.copy(dynamicColors = dynamicColors) }
            }
        }

        launchScoped {
            customizationRepository.observeServicesStyle().collect { servicesStyle ->
                uiState.update { it.copy(servicesStyle = servicesStyle) }
            }
        }

        launchScoped {
            customizationRepository.observeShowNextCode().collect { showNextCode ->
                uiState.update { it.copy(showNextCode = showNextCode) }
            }
        }

        launchScoped {
            customizationRepository.observeHideCodes().collect { hideCodes ->
                uiState.update { it.copy(hideCodes = hideCodes) }
            }
        }

        launchScoped {
            customizationRepository.observeAutoFocusSearch().collect { autoFocusSearch ->
                uiState.update { it.copy(autoFocusSearch = autoFocusSearch) }
            }
        }

        launchScoped {
            settingsRepository.observeShowBackupNotice().collect { showBackupNotice ->
                uiState.update { it.copy(showBackupNotice = showBackupNotice) }
            }
        }
    }

    fun setSelectedTheme(selectedTheme: SelectedTheme) {
        launchScoped {
            customizationRepository.setSelectedTheme(selectedTheme)
        }
    }

    fun setServiceStyle(servicesStyle: ServicesStyle) {
        launchScoped {
            customizationRepository.setServicesStyle(servicesStyle)
        }
    }

    fun toggleShowNextToken() {
        launchScoped {
            customizationRepository.setShowNextCode(uiState.value.showNextCode.not())
        }
    }

    fun toggleAutoFocusSearch() {
        launchScoped {
            customizationRepository.setAutoFocusSearch(uiState.value.autoFocusSearch.not())
        }
    }

    fun toggleShowBackupNotice() {
        launchScoped {
            settingsRepository.setShowBackupNotice(uiState.value.showBackupNotice.not())
        }
    }

    fun toggleHideTokens() {
        launchScoped {
            customizationRepository.setHideCodes(uiState.value.hideCodes.not())
        }
    }

    fun toggleDynamicColors() {
        launchScoped {
            customizationRepository.setDynamicColors(uiState.value.dynamicColors.not())
        }
    }
}