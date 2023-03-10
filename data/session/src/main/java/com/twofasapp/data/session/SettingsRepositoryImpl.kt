package com.twofasapp.data.session

import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.data.session.domain.AppSettings
import com.twofasapp.data.session.domain.SelectedTheme
import com.twofasapp.data.session.domain.ServicesStyle
import com.twofasapp.data.session.local.SettingsLocalSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class SettingsRepositoryImpl(
    private val dispatchers: Dispatchers,
    private val local: SettingsLocalSource,
) : SettingsRepository {

    override fun observeAppSettings(): Flow<AppSettings> {
        return local.observeAppSettings()
    }

    override fun getAppSettings(): AppSettings {
        return local.getAppSettings()
    }

    override suspend fun setShowNextCode(showNextCode: Boolean) {
        withContext(dispatchers.io) {
            local.setShowNextCode(showNextCode)
        }
    }

    override suspend fun setSelectedTheme(selectedTheme: SelectedTheme) {
        withContext(dispatchers.io) {
            local.setSelectedTheme(selectedTheme)
        }
    }

    override suspend fun setServicesStyle(servicesStyle: ServicesStyle) {
        withContext(dispatchers.io) {
            local.setServicesStyle(servicesStyle)
        }
    }

    override suspend fun setAutoFocusSearch(autoFocusSearch: Boolean) {
        withContext(dispatchers.io) {
            local.setAutoFocusSearch(autoFocusSearch)
        }
    }
}