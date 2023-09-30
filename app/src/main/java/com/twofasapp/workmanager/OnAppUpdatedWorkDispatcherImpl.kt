package com.twofasapp.workmanager

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import timber.log.Timber

class OnAppUpdatedWorkDispatcherImpl(
    private val context: Context,
) : OnAppUpdatedWorkDispatcher {

    override fun dispatch() {
        val request: OneTimeWorkRequest = OneTimeWorkRequestBuilder<OnAppUpdatedWork>()
            .build()

        Timber.d("Append new OnAppUpdatedWork")
        WorkManager.getInstance(context)
            .enqueueUniqueWork("OnAppUpdatedWork", ExistingWorkPolicy.APPEND_OR_REPLACE, request)
    }
}