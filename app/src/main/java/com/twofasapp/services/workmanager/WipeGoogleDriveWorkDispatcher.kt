package com.twofasapp.services.workmanager

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.twofasapp.di.WorkDispatcher
import timber.log.Timber

class WipeGoogleDriveWorkDispatcher(private val context: Context) : WorkDispatcher {

    fun dispatch() {
        Constraints.Builder().build()

        val request = OneTimeWorkRequestBuilder<WipeGoogleDriveWork>().build()

        if (isThereWorkScheduled().not()) {
            Timber.d("Append new WipeGoogleDriveWork")
            WorkManager.getInstance(context).enqueueUniqueWork("WipeGoogleDriveWork", ExistingWorkPolicy.APPEND_OR_REPLACE, request)
        }
    }

    private fun isThereWorkScheduled(): Boolean {
        return WorkManager.getInstance(context).getWorkInfosForUniqueWork("WipeGoogleDriveWork").get()
            .find { it.state == WorkInfo.State.BLOCKED || it.state == WorkInfo.State.ENQUEUED }?.let {
                Timber.d("There is a work (${it.id}) in queue - do not append")
                true
            } ?: false
    }
}