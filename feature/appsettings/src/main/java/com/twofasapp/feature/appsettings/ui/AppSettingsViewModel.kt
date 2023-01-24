package com.twofasapp.feature.appsettings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.data.session.SettingsRepository
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
                    it
                }
            }
        }
    }

    fun toggleShowNextToken() {

    }

    fun setAppTheme() {

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