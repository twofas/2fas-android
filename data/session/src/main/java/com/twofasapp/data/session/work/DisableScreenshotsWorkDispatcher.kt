package com.twofasapp.data.session.work

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.twofasapp.di.WorkDispatcher
import java.util.concurrent.TimeUnit

class DisableScreenshotsWorkDispatcher(
    private val context: Context
) : WorkDispatcher {

    fun dispatch() {
        val request = OneTimeWorkRequestBuilder<DisableScreenshotsWork>()
            .setInitialDelay(5, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork("DisableScreenshotsWork", ExistingWorkPolicy.REPLACE, request)
    }
}