package com.twofasapp.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.logger.Flog
import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.common.storage.longPref
import com.twofasapp.migration.MigrateUnknownServices
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import timber.log.Timber

class OnAppUpdatedWork(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {

    private val dispatchers: Dispatchers by inject()
    private val appBuild: AppBuild by inject()
    private val migrateUnknownServices: MigrateUnknownServices by inject()
    private val prefs: Prefs by lazy { Prefs(get()) }

    private class Prefs(dataStoreOwner: DataStoreOwner) : DataStoreOwner by dataStoreOwner {
        val currentAppVersionCode by longPref(
            name = "currentAppVersionCode",
            default = 0L,
        )
    }

    override suspend fun doWork(): Result {
        return withContext(dispatchers.io) {
            try {
                if (appBuild.versionCode.toLong() == prefs.currentAppVersionCode.get()) {
                    Timber.d("Migration not needed")
                    return@withContext Result.success()
                }

                Timber.d("Start migration: ${appBuild.versionCode.toLong()} -> ${prefs.currentAppVersionCode.get()}")
                Flog.persist(tag = "AppUpdate", message = "Migration started: ${prefs.currentAppVersionCode.get()} -> ${appBuild.versionCode}")

                Timber.d("Migrate: Unknown services")
                migrateUnknownServices.invoke()

                Timber.d("Migration done!")
                Flog.persist(tag = "AppUpdate", message = "Migration done")
                prefs.currentAppVersionCode.set(appBuild.versionCode.toLong())

                Result.success()
            } catch (e: Exception) {
                Timber.e(e)
                Flog.persist(tag = "AppUpdate", message = "Migration failed")
                Flog.persist(tag = "AppUpdate", throwable = e)

                Result.failure()
            }
        }
    }
}