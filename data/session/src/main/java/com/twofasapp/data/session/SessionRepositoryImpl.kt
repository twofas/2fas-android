package com.twofasapp.data.session

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.instacart.library.truetime.TrueTime
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.common.storage.longPref
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.session.local.SessionLocalSource
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
    dataStoreOwner: DataStoreOwner,
) : SessionRepository, DataStoreOwner by dataStoreOwner {

    private val appUpdateLastCheckVersion by longPref(
        name = "appUpdateLastCheckVersion",
        default = 0L,
    )

    private val timeDelta by longPref(
        name = "timeDelta",
        default = 0L,
    )

    override suspend fun showAppUpdate(): Boolean {
        return appBuild.versionCode.toLong() != appUpdateLastCheckVersion.get()
    }

    override suspend fun setAppUpdateDisplayed() {
        appUpdateLastCheckVersion.set(appBuild.versionCode.toLong())
    }

    override suspend fun setRateAppDisplayed(isDisplayed: Boolean) {
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

    override fun observeShowPassBanner(): Flow<Boolean> {
        return local.observePassBannerDismissTimestamp().map { dismissTimestamp ->
            if (context.isAppInstalled(packageName = "com.twofasapp.pass")) {
                return@map false
            }

            // Check if app installed for at least 3 months
            val currentTime = timeProvider.systemCurrentTime()
            val appInstallTimestamp = local.getAppInstallTimestamp()
            if ((currentTime - appInstallTimestamp) < Duration.ofDays(90).toMillis()) {
                return@map false
            }

            // Check if 3 months elapsed since last dismiss
            (currentTime - dismissTimestamp) >= Duration.ofDays(90).toMillis()
        }
    }

    override suspend fun resetPassBannerDismiss() {
        local.setPassBannerDismissTimestamp(timeProvider.systemCurrentTime())
    }

    override suspend fun disablePassBanner() {
        local.setPassBannerDismissTimestamp(timeProvider.systemCurrentTime() + Duration.ofDays(365 * 100).toMillis())
    }

    override fun observeAppReviewPrompted(): Flow<Boolean> {
        return local.observeAppReviewPromptedTimestamp().map { it > 0L }
    }

    override suspend fun markAppReviewPrompted() {
        withContext(dispatchers.io) {
            local.setAppReviewPromptedTimestamp(timeProvider.systemCurrentTime())
        }
    }

    private suspend fun recalculate(): Boolean {
        Timber.d("TrueTime: sync...")
        return if (TrueTime.isInitialized()) {
            Timber.d("TrueTime: synced - ${TrueTime.now()}")
            val newDelta = TrueTime.now().time - System.currentTimeMillis()
            timeDelta.set(newDelta)

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