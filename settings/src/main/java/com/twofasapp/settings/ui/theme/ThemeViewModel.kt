package com.twofasapp.settings.ui.theme

import androidx.lifecycle.viewModelScope
import com.twofasapp.base.BaseViewModel
import com.twofasapp.base.dispatcher.Dispatchers
import com.twofasapp.design.theme.ThemeState
import com.twofasapp.prefs.model.AppTheme
import com.twofasapp.prefs.usecase.AppThemePreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ThemeViewModel(
    private val dispatchers: Dispatchers,
    private val appThemePreference: AppThemePreference,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ThemeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(dispatchers.io()) {
            appThemePreference.flow().collect {
                _uiState.update { state -> state.copy(theme = it) }
            }
        }
    }

    fun changeTheme(theme: AppTheme) {
        if (theme == uiState.value.theme) {
            return
        }

        appThemePreference.put(theme)

        ThemeState.applyTheme(theme)

        _uiState.update { it.copy(recreateActivity = true) }
    }
}