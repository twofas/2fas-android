package com.twofasapp.data.session.local

import com.twofasapp.data.session.domain.AppSettings
import com.twofasapp.data.session.domain.SelectedTheme
import com.twofasapp.data.session.domain.ServicesStyle
import com.twofasapp.storage.PlainPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class SettingsLocalSource(
    private val preferences: PlainPreferences
) {

    companion object {
        private const val KeyShowNextCode = "showNextToken"
        private const val KeySelectedTheme = "selectedTheme"
        private const val KeyServicesStyle = "servicesStyle"
        private const val KeyAutoFocusSearch = "autoFocusSearch"

    }

    private val appSettingsFlow: MutableStateFlow<AppSettings> by lazy {
        MutableStateFlow(getAppSettings())
    }

    fun observeAppSettings(): Flow<AppSettings> {
        return appSettingsFlow
    }

    fun getAppSettings(): AppSettings {
        return AppSettings(
            showNextCode = preferences.getBoolean(KeyShowNextCode) ?: false,
            autoFocusSearch = preferences.getBoolean(KeyAutoFocusSearch) ?: false,
            selectedTheme = preferences.getString(KeySelectedTheme)?.let { SelectedTheme.valueOf(it) } ?: SelectedTheme.Auto,
            servicesStyle = preferences.getString(KeyServicesStyle)?.let { ServicesStyle.valueOf(it) } ?: ServicesStyle.Default,
        )
    }

    fun setShowNextCode(showNextCode: Boolean) {
        appSettingsFlow.update { it.copy(showNextCode = showNextCode) }
        preferences.putBoolean(KeyShowNextCode, showNextCode)
    }

    fun setSelectedTheme(selectedTheme: SelectedTheme) {
        appSettingsFlow.update { it.copy(selectedTheme = selectedTheme) }
        preferences.putString(KeySelectedTheme, selectedTheme.name)
    }

    fun setServicesStyle(servicesStyle: ServicesStyle) {
        appSettingsFlow.update { it.copy(servicesStyle = servicesStyle) }
        preferences.putString(KeyServicesStyle, servicesStyle.name)
    }

    fun setAutoFocusSearch(autoFocusSearch: Boolean) {
        appSettingsFlow.update { it.copy(autoFocusSearch = autoFocusSearch) }
        preferences.putBoolean(KeyAutoFocusSearch, autoFocusSearch)
    }
}