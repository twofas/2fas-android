package com.twofasapp.data.session.local

import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.environment.BuildVariant
import com.twofasapp.data.session.domain.AppSettings
import com.twofasapp.storage.PlainPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class SettingsLocalSource(
    private val preferences: PlainPreferences,
    private val appBuild: AppBuild,
) {

    companion object {
        private const val KeyShowBackupNotice = "showBackupNotice"
        private const val KeySendCrashLogs = "sendCrashLogs"
        private const val KeyAllowScreenshots = "allowScreenshots"
    }

    private val appSettingsFlow: MutableStateFlow<AppSettings> by lazy {
        MutableStateFlow(getAppSettings())
    }

    fun observeAppSettings(): Flow<AppSettings> {
        return appSettingsFlow
    }

    fun getAppSettings(): AppSettings {
        return AppSettings(
            showBackupNotice = preferences.getBoolean(KeyShowBackupNotice) ?: true,
            sendCrashLogs = preferences.getBoolean(KeySendCrashLogs) ?: true,
            allowScreenshots = preferences.getBoolean(KeyAllowScreenshots) ?: when (appBuild.buildVariant) {
                BuildVariant.Release -> false
                BuildVariant.ReleaseLocal -> true
                BuildVariant.Debug -> true
            },
        )
    }

    fun setShowBackupNotice(showBackupNotice: Boolean) {
        appSettingsFlow.update { it.copy(showBackupNotice = showBackupNotice) }
        preferences.putBoolean(KeyShowBackupNotice, showBackupNotice)
    }

    fun setSendCrashLogs(sendCrashLogs: Boolean) {
        appSettingsFlow.update { it.copy(sendCrashLogs = sendCrashLogs) }
        preferences.putBoolean(KeySendCrashLogs, sendCrashLogs)
    }

    fun setAllowScreenshots(allow: Boolean) {
        appSettingsFlow.update { it.copy(allowScreenshots = allow) }
        preferences.putBoolean(KeyAllowScreenshots, allow)
    }
}