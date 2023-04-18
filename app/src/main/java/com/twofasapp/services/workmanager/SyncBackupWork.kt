package com.twofasapp.services.workmanager

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.twofasapp.backup.domain.SyncBackupTrigger
import com.twofasapp.backup.domain.SyncBackupTrigger.SERVICES_CHANGED
import com.twofasapp.entity.SyncBackupResult
import com.twofasapp.usecases.backup.SyncBackupServices
import io.reactivex.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncBackupWork(
    appContext: Context,
    workerParams: WorkerParameters
) : RxWorker(appContext, workerParams), KoinComponent {

    companion object {
        const val ARG_TRIGGER = "trigger"
        const val ARG_PASSWORD = "password"
    }

    private val syncBackupServices: SyncBackupServices by inject()

    override fun createWork(): Single<Result> {
        return syncBackupServices.execute(
            SyncBackupServices.Params(
                syncBackupTrigger = SyncBackupTrigger.values().firstOrNull { it.name == inputData.getString(ARG_TRIGGER) } ?: SERVICES_CHANGED,
                password = inputData.getString(ARG_PASSWORD)
            )
        )
            .map {
                when (it) {
                    is SyncBackupResult.Success -> Result.success()
                    is SyncBackupResult.Failure -> when (it.trigger) {
                        SyncBackupTrigger.APP_BACKGROUND,
                        SyncBackupTrigger.WIPE_DATA -> Result.retry()
                        else -> Result.success()
                    }
                }
            }

    }
}