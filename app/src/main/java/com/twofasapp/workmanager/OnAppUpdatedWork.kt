package com.twofasapp.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.prefs.usecase.CurrentAppVersionPreference
import com.twofasapp.usecases.ClearObsoletePrefsCase
import com.twofasapp.usecases.MigrateBoxToRoomCase
import com.twofasapp.usecases.MigratePinCase
import com.twofasapp.usecases.MigrateUnknownServicesCase
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
    private val clearObsoletePrefsCase: ClearObsoletePrefsCase by inject()
    private val migratePinCase: MigratePinCase by inject()
    private val migrateUnknownServicesCase: MigrateUnknownServicesCase by inject()
    private val migrateBoxToRoomCase: MigrateBoxToRoomCase by inject()

    override suspend fun doWork(): Result {
        return withContext(dispatchers.io) {
            try {
                if (appBuild.versionCode.toLong() == currentAppVersionPreference.get()) {
                    Timber.d("Migration not needed")
                    return@withContext Result.success()
                }

                Timber.d("Start migration: ${appBuild.versionCode.toLong()} -> ${currentAppVersionPreference.get()}")

                Timber.d("Migrate: Obsolete prefs")
                clearObsoletePrefsCase.invoke()

                Timber.d("Migrate: Box to Room")
                migrateBoxToRoomCase.invoke()

                Timber.d("Migrate: Unknown services")
                migrateUnknownServicesCase.invoke()

                Timber.d("Migrate: Pin")
                migratePinCase.invoke()

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
