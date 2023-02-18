package com.twofasapp.data.session

import com.twofasapp.data.session.domain.AppSettings
import com.twofasapp.data.session.domain.SelectedTheme
import com.twofasapp.data.session.local.SettingsLocalSource
import kotlinx.coroutines.flow.Flow

internal class SettingsRepositoryImpl(
    private val local: SettingsLocalSource,
) : SettingsRepository {

    override fun observeAppSettings(): Flow<AppSettings> {
        return local.observeAppSettings()
    }

    override suspend fun setShowNextToken(showNextToken: Boolean) {
        local.setShowNextToken(showNextToken)
    }

    override suspend fun setSelectedTheme(selectedTheme: SelectedTheme) {
        local.setSelectedTheme(selectedTheme)
    }
}