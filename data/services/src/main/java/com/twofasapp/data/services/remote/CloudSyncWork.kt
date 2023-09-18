package com.twofasapp.data.services.remote

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.twofasapp.common.ktx.enumValueOrNull
import com.twofasapp.data.services.domain.CloudSyncTrigger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CloudSyncWork(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    companion object {
        const val ArgTrigger = "trigger"
        const val ArgPassword = "password"
    }

    private val cloudSync: CloudSync by inject()

    override suspend fun doWork(): Result {
        val jobResult = cloudSync.execute(
            trigger = enumValueOrNull(inputData.getString(ArgTrigger)) ?: CloudSyncTrigger.ServicesChanged,
            password = inputData.getString(ArgPassword)
        )

        return when (jobResult) {
            is CloudSyncResult.Success -> Result.success()
            is CloudSyncResult.Failure -> Result.success()
        }
    }
}