package com.twofasapp.data.session

import com.twofasapp.data.session.domain.AppSettings
import com.twofasapp.data.session.domain.SelectedTheme
import com.twofasapp.data.session.domain.ServicesStyle
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeAppSettings(): Flow<AppSettings>
    fun getAppSettings(): AppSettings
    suspend fun setShowNextCode(showNextCode: Boolean)
    suspend fun setSelectedTheme(selectedTheme: SelectedTheme)
    suspend fun setServicesStyle(servicesStyle: ServicesStyle)
    suspend fun setAutoFocusSearch(autoFocusSearch: Boolean)
}