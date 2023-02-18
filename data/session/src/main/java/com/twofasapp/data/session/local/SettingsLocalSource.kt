package com.twofasapp.data.session.local

import com.twofasapp.common.ktx.camelCaseBeginUpper
import com.twofasapp.data.session.domain.AppSettings
import com.twofasapp.data.session.domain.SelectedTheme
import com.twofasapp.storage.PlainPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class SettingsLocalSource(
    private val preferences: PlainPreferences
) {

    companion object {
        private const val KeyShowNextToken = "showNextToken"

        // Legacy format was using Json serializer to store enum, example:
        // <string name="appTheme">&quot;LIGHT&quot;</string> (<string name="appTheme">"LIGHT</string>)
        private const val KeyAppTheme = "appTheme"

    }

    private val appSettingsFlow: MutableStateFlow<AppSettings> by lazy {
        MutableStateFlow(
            AppSettings(
                showNextToken = preferences.getBoolean(KeyShowNextToken) ?: false,
                selectedTheme = preferences.getString(KeyAppTheme)?.let {
                    SelectedTheme.valueOf(it.replace("\"", "").lowercase().camelCaseBeginUpper())
                } ?: SelectedTheme.Auto
            )
        )
    }

    fun observeAppSettings(): Flow<AppSettings> {
        return appSettingsFlow
    }

    suspend fun setShowNextToken(showNextToken: Boolean) {
        appSettingsFlow.update { it.copy(showNextToken = showNextToken) }
        preferences.putBoolean(KeyShowNextToken, showNextToken)
    }

    suspend fun setSelectedTheme(selectedTheme: SelectedTheme) {
        appSettingsFlow.update { it.copy(selectedTheme = selectedTheme) }
        preferences.putString(KeyAppTheme, selectedTheme.name.uppercase()) // TODO Replace
    }
}