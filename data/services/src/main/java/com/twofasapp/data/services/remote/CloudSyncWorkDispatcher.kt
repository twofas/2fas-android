package com.twofasapp.data.services.remote

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.twofasapp.data.services.domain.CloudSyncTrigger
import com.twofasapp.prefs.model.RemoteBackupStatusEntity
import com.twofasapp.prefs.usecase.RemoteBackupStatusPreference
import timber.log.Timber

class CloudSyncWorkDispatcher(
    private val context: Context,
    private val remoteBackupStatusPreference: RemoteBackupStatusPreference,
) {

    fun tryDispatch(trigger: CloudSyncTrigger, password: String? = null) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<CloudSyncWork>()
            .setInputData(
                Data.Builder().apply {
                    putString(CloudSyncWork.ArgTrigger, trigger.name)
                    password?.let { putString(CloudSyncWork.ArgPassword, it) }
                }.build()
            )
            .setConstraints(constraints)
            .build()

        if (remoteBackupStatusPreference.get().state != RemoteBackupStatusEntity.State.ACTIVE) {
            return
        }

        val enqueuedWork = findEnqueuedWork()

        // No jobs in queue -> append new one
        if (enqueuedWork == null) {
            Timber.d("Append new SyncBackupWork")
            WorkManager.getInstance(context).enqueueUniqueWork("SyncBackupWork", ExistingWorkPolicy.APPEND_OR_REPLACE, request)
        }

        // There is a job in queue with retry policy -> replace with new one
        if ((enqueuedWork != null && enqueuedWork.runAttemptCount > 0) || enqueuedWork?.state == WorkInfo.State.BLOCKED) {
            Timber.d("Append new SyncBackupWork by replacing retry work")
            WorkManager.getInstance(context).enqueueUniqueWork("SyncBackupWork", ExistingWorkPolicy.REPLACE, request)
        }
    }

    private fun findEnqueuedWork() = WorkManager.getInstance(context).getWorkInfosForUniqueWork("SyncBackupWork").get()
        .find { it.state == WorkInfo.State.BLOCKED || it.state == WorkInfo.State.ENQUEUED }?.let {
            Timber.d("There is a work in queue: id=${it.id} runAttemptCount=${it.runAttemptCount}")
            it
        }
}