package com.twofasapp.data.session

import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.environment.BuildVariant
import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.common.storage.booleanPref
import com.twofasapp.data.session.domain.AppSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

internal class SettingsRepositoryImpl(
    dataStoreOwner: DataStoreOwner,
    private val dispatchers: Dispatchers,
    private val appBuild: AppBuild,
) : SettingsRepository, DataStoreOwner by dataStoreOwner {

    private val scope = CoroutineScope(dispatchers.io)

    private val showBackupNotice by booleanPref(name = "showBackupNotice", default = true)
    private val sendCrashLogs by booleanPref(name = "sendCrashLogs", default = true)
    private val allowScreenshots by booleanPref(
        name = "allowScreenshots",
        default = when (appBuild.buildVariant) {
            BuildVariant.Release -> false
            BuildVariant.ReleaseLocal -> true
            BuildVariant.Debug -> true
        },
    )

    private val appSettings: StateFlow<AppSettings> =
        combineAppSettings().stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = runBlocking { combineAppSettings().first() },
        )

    override fun observeAppSettings(): Flow<AppSettings> {
        return appSettings
    }

    override fun getAppSettings(): AppSettings {
        return appSettings.value
    }

    override fun observeShowBackupNotice(): Flow<Boolean> {
        return showBackupNotice.asFlow()
    }

    override suspend fun setShowBackupNotice(showBackupNotice: Boolean) {
        withContext(dispatchers.io) { this@SettingsRepositoryImpl.showBackupNotice.set(showBackupNotice) }
    }

    override suspend fun setSendCrashLogs(sendCrashLogs: Boolean) {
        withContext(dispatchers.io) { this@SettingsRepositoryImpl.sendCrashLogs.set(sendCrashLogs) }
    }

    override suspend fun setAllowScreenshots(allow: Boolean) {
        withContext(dispatchers.io) { this@SettingsRepositoryImpl.allowScreenshots.set(allow) }
    }

    private fun combineAppSettings(): Flow<AppSettings> {
        return combine(
            showBackupNotice.asFlow(),
            sendCrashLogs.asFlow(),
            allowScreenshots.asFlow(),
        ) { backup, crash, screenshots ->
            AppSettings(
                showBackupNotice = backup,
                sendCrashLogs = crash,
                allowScreenshots = screenshots,
            )
        }
    }
}