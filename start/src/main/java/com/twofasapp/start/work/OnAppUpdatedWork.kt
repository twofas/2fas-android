package com.twofasapp.start.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.twofasapp.base.dispatcher.Dispatchers
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.prefs.usecase.CurrentAppVersionPreference
import com.twofasapp.start.domain.ClearObsoletePrefsCase
import com.twofasapp.start.domain.MigrateBoxToRoomCase
import com.twofasapp.start.domain.MigratePinCase
import com.twofasapp.start.domain.MigrateUnknownServicesCase
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
        return withContext(dispatchers.io()) {
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
