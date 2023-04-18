package com.twofasapp.data.session

import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.session.local.SessionLocalSource
import com.twofasapp.prefs.model.RemoteBackupStatus
import com.twofasapp.prefs.usecase.AppUpdateLastCheckVersionPreference
import com.twofasapp.prefs.usecase.RemoteBackupStatusPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Duration

internal class SessionRepositoryImpl(
    private val dispatchers: Dispatchers,
    private val appBuild: AppBuild,
    private val local: SessionLocalSource,
    private val timeProvider: TimeProvider,
    private val remoteBackupStatusPreference: RemoteBackupStatusPreference,
    private val appUpdateLastCheckVersionPreference: AppUpdateLastCheckVersionPreference
) : SessionRepository {

    override suspend fun isOnboardingDisplayed(): Boolean {
        return withContext(dispatchers.io) {
            local.isOnboardingDisplayed()
        }
    }

    override suspend fun showBackupReminder(): Boolean {
        return true
    }

    override fun showAppUpdate(): Boolean {
        return appBuild.versionCode.toLong() != appUpdateLastCheckVersionPreference.get()
    }

    override fun setAppUpdateDisplayed() {
        appUpdateLastCheckVersionPreference.put(appBuild.versionCode.toLong())
    }

    override suspend fun setOnboardingDisplayed(isDisplayed: Boolean) {
        withContext(dispatchers.io) {
            local.setOnboardingDisplayed(isDisplayed)
        }
    }

    override suspend fun setRateAppDisplayed(isDisplayed: Boolean) {

    }

    override fun observeBackupEnabled(): Flow<Boolean> {
        return remoteBackupStatusPreference.flow(true).map {
            it.state == RemoteBackupStatus.State.ACTIVE
        }
    }

    override fun observeShowBackupReminder(): Flow<Boolean> {
        return local.observeBackupReminderTimestamp().map { nextTimestamp ->
            timeProvider.systemCurrentTime() > nextTimestamp
        }
    }

    override fun resetBackupReminder() {
        local.setBackupReminderTimestamp(
            timeProvider.systemCurrentTime() + Duration.ofDays(21).toMillis()
        )
    }
}