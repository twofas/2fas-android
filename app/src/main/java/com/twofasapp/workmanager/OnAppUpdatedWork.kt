package com.twofasapp.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.migration.ClearObsoletePrefs
import com.twofasapp.migration.MigrateBoxToRoom
import com.twofasapp.migration.MigratePin
import com.twofasapp.migration.MigrateUnknownServices
import com.twofasapp.prefs.usecase.CurrentAppVersionPreference
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class OnAppUpdatedWork(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val dispatchers: Dispatchers by inject()
    private val appBuild: AppBuild by inject()
    private val currentAppVersionPreference: CurrentAppVersionPreference by inject()
    private val clearObsoletePrefs: ClearObsoletePrefs by inject()
    private val migratePin: MigratePin by inject()
    private val migrateUnknownServices: MigrateUnknownServices by inject()
    private val migrateBoxToRoom: MigrateBoxToRoom by inject()

    override suspend fun doWork(): Result {
        return withContext(dispatchers.io) {
            try {
                if (appBuild.versionCode.toLong() == currentAppVersionPreference.get()) {
                    Timber.d("Migration not needed")
                    return@withContext Result.success()
                }

                Timber.d("Start migration: ${appBuild.versionCode.toLong()} -> ${currentAppVersionPreference.get()}")

                Timber.d("Migrate: Obsolete prefs")
                clearObsoletePrefs.invoke()

                Timber.d("Migrate: Box to Room")
                migrateBoxToRoom.invoke()

                Timber.d("Migrate: Unknown services")
                migrateUnknownServices.invoke()

                Timber.d("Migrate: Pin")
                migratePin.invoke()

                Timber.d("Migration done!")
                currentAppVersionPreference.put(appBuild.versionCode.toLong())

                Result.success()
            } catch (e: Exception) {
                Timber.e(e)

                Result.failure()
            }
        }
    }
}
