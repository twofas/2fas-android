package com.twofasapp.data.session.local

import com.twofasapp.data.session.domain.AppSettings
import com.twofasapp.storage.PlainPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class SettingsLocalSource(private val preferences: PlainPreferences) {

    companion object {
        private const val KeyShowNextToken = "showNextToken"
    }

    private val appSettingsFlow: MutableStateFlow<AppSettings> by lazy {
        MutableStateFlow(
            AppSettings(
                showNextToken = preferences.getBoolean(KeyShowNextToken) ?: false
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
}