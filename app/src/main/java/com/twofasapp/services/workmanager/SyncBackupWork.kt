package com.twofasapp.services.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.twofasapp.common.ktx.enumValueOrNull
import com.twofasapp.data.services.domain.CloudSyncTrigger
import com.twofasapp.data.services.remote.CloudSyncJob
import com.twofasapp.data.services.remote.CloudSyncJobResult
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncBackupWork(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    companion object {
        const val ArgTrigger = "trigger"
        const val ArgPassword = "password"
    }

    private val cloudSyncJob: CloudSyncJob by inject()

    override suspend fun doWork(): Result {
        val jobResult = cloudSyncJob.execute(
            trigger = enumValueOrNull(inputData.getString(ArgTrigger)) ?: CloudSyncTrigger.ServicesChanged,
            password = inputData.getString(ArgPassword)
        )

        return when (jobResult) {
            is CloudSyncJobResult.Success -> Result.success()
            is CloudSyncJobResult.Failure -> {
                when (jobResult.trigger) {
                    CloudSyncTrigger.AppBackground,
                    CloudSyncTrigger.WipeData -> Result.retry()

                    else -> Result.success()
                }
            }
        }
    }
}