package com.twofasapp.data.session

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.instacart.library.truetime.TrueTime
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.session.local.SessionLocalSource
import com.twofasapp.prefs.model.RemoteBackupStatusEntity
import com.twofasapp.prefs.usecase.AppUpdateLastCheckVersionPreference
import com.twofasapp.prefs.usecase.RemoteBackupStatusPreference
import com.twofasapp.prefs.usecase.TimeDeltaPreference
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.Duration

internal class SessionRepositoryImpl(
    private val context: Context,
    private val dispatchers: Dispatchers,
    private val appBuild: AppBuild,
    private val local: SessionLocalSource,
    private val timeProvider: TimeProvider,
    private val remoteBackupStatusPreference: RemoteBackupStatusPreference,
    private val appUpdateLastCheckVersionPreference: AppUpdateLastCheckVersionPreference,
    private val timeDeltaPreference: TimeDeltaPreference,
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
            it.state == RemoteBackupStatusEntity.State.ACTIVE
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

    override suspend fun getAppInstallTimestamp(): Long {
        return local.getAppInstallTimestamp()
    }

    override suspend fun markAppInstalled() {
        local.markAppInstalled()
    }

    override suspend fun recalculateTimeDelta() {
        var retries = 30

        while (recalculate().not() && retries > 0) {
            delay(2000)
            retries--
        }
    }

    override suspend fun noCompanionAppFromTimestamp(): Long? {
        val hasCompanionAppInstalled = context.isAppInstalled(packageName = "com.twofasapp.pass")

        if (hasCompanionAppInstalled) {
            local.setNoCompanionAppFromTimestamp(null)
            return null
        }

        val noCompanionAppFromTimestamp = local.getNoCompanionAppFromTimestamp()

        if (noCompanionAppFromTimestamp == null) {
            val now = timeProvider.realCurrentTime()
            local.setNoCompanionAppFromTimestamp(now)
            return now
        }

        return noCompanionAppFromTimestamp
    }

    private fun recalculate(): Boolean {
        Timber.d("TrueTime: sync...")
        return if (TrueTime.isInitialized()) {
            Timber.d("TrueTime: synced - ${TrueTime.now()}")
            val newDelta = TrueTime.now().time - System.currentTimeMillis()
            timeDeltaPreference.put(newDelta)

            true
        } else {
            false
        }
    }

    fun Context.isAppInstalled(packageName: String): Boolean {
        return try {
            val pm = packageManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                pm.getPackageInfo(packageName, 0)
            }
            true
        } catch (_: Exception) {
            false
        }
    }
}