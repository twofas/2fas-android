package com.twofasapp.data.session

import com.twofasapp.data.session.domain.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeAppSettings(): Flow<AppSettings>
    fun getAppSettings(): AppSettings
    fun observeShowBackupNotice(): Flow<Boolean>
    suspend fun setShowBackupNotice(showBackupNotice: Boolean)
    suspend fun setSendCrashLogs(sendCrashLogs: Boolean)
    suspend fun setAllowScreenshots(allow: Boolean)
}