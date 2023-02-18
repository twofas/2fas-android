package com.twofasapp.feature.appsettings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.ktx.launchScoped
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.data.session.domain.SelectedTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppSettingsViewModel(
    private val dispatchers: Dispatchers,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState = MutableStateFlow(AppSettingsUiState())

    init {
        viewModelScope.launch(dispatchers.io) {
            settingsRepository.observeAppSettings().collect { settings ->
                uiState.update {
                    it.copy(
                        showNextToken = settings.showNextToken,
                        selectedTheme = settings.selectedTheme,
                    )
                }
            }
        }
    }

    fun setSelectedTheme(selectedTheme: SelectedTheme) {
        launchScoped {
            settingsRepository.setSelectedTheme(selectedTheme)
        }
    }

    fun toggleShowNextToken() {
        launchScoped {
            settingsRepository.setShowNextToken(uiState.value.showNextToken.not())
        }
    }


//    init {
//        viewModelScope.launch(dispatchers.io()) {
//            appThemePreference.flow().collect {
//                _uiState.update { state -> state.copy(theme = it) }
//            }
//        }
//    }
//
//    fun changeTheme(theme: AppTheme) {
//        if (theme == uiState.value.theme) {
//            return
//        }
//
//        appThemePreference.put(theme)
//
//        ThemeState.applyTheme(theme)
//
//        _uiState.update { it.copy(recreateActivity = true) }
//    }
}