package com.twofasapp.settings.ui.main

import androidx.lifecycle.viewModelScope
import com.twofasapp.base.BaseViewModel
import com.twofasapp.base.dispatcher.Dispatchers
import com.twofasapp.prefs.usecase.AppThemePreference
import com.twofasapp.prefs.usecase.SendCrashLogsPreference
import com.twofasapp.prefs.usecase.ShowNextTokenPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class SettingsMainViewModel(
    private val dispatchers: Dispatchers,
    private val appThemePreference: AppThemePreference,
    private val showNextTokenPreference: ShowNextTokenPreference,
    private val sendCrashLogsPreference: SendCrashLogsPreference,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(SettingsMainUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {

            launch(dispatchers.io()) {
                appThemePreference.flow().collect {
                    _uiState.update { state -> state.copy(theme = it) }
                }
            }

            launch(dispatchers.io()) {
                showNextTokenPreference.flow().collect {
                    _uiState.update { state -> state.copy(showNextToken = it) }
                }
            }

            launch(dispatchers.io()) {
                sendCrashLogsPreference.flow().collect {
                    _uiState.update { state -> state.copy(sendCrashLogs = it) }
                }
            }
        }
    }

    fun changeShowNextToken(isChecked: Boolean) {
        viewModelScope.launch(dispatchers.io()) {
            showNextTokenPreference.put(isChecked)
        }
    }

    fun changeSendCrashLogs(isChecked: Boolean) {
        viewModelScope.launch(dispatchers.io()) {
            sendCrashLogsPreference.put(isChecked)
        }
    }
}
