package com.twofasapp.data.session

import com.twofasapp.data.session.domain.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeAppSettings(): Flow<AppSettings>
    suspend fun setShowNextToken(showNextToken: Boolean)
}