package com.twofasapp.time.domain.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import timber.log.Timber

class SyncTimeWorkDispatcherImpl(
    private val context: Context,
) : SyncTimeWorkDispatcher {

    override fun dispatch() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request: OneTimeWorkRequest = OneTimeWorkRequestBuilder<SyncTimeWork>()
            .setConstraints(constraints)
            .build()

        val enqueuedWork = findEnqueuedWork()

        // No jobs in queue -> append new one
        if (enqueuedWork == null) {
            Timber.d("Append new SyncTimeWork")
            WorkManager.getInstance(context).enqueueUniqueWork("SyncTimeWork", ExistingWorkPolicy.APPEND_OR_REPLACE, request)
        }

        // There is a job in queue with retry policy -> replace with new one
        if ((enqueuedWork != null && enqueuedWork.runAttemptCount > 0) || enqueuedWork?.state == WorkInfo.State.BLOCKED) {
            Timber.d("Append new SyncTimeWork by replacing retry work")
            WorkManager.getInstance(context).enqueueUniqueWork("SyncTimeWork", ExistingWorkPolicy.REPLACE, request)
        }
    }

    private fun findEnqueuedWork() = WorkManager.getInstance(context).getWorkInfosForUniqueWork("SyncTimeWork").get()
        .find { it.state == WorkInfo.State.BLOCKED || it.state == WorkInfo.State.ENQUEUED }?.let {
            Timber.d("There is a work in queue: id=${it.id} runAttemptCount=${it.runAttemptCount}")
            it
        }
}