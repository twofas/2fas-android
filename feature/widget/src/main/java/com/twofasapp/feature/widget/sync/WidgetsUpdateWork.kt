package com.twofasapp.feature.widget.sync

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.twofasapp.feature.widget.GlanceWidget

class WidgetsUpdateWork(
    val context: Context,
    val params: WorkerParameters,
) : CoroutineWorker(context, params) {

    companion object {
        fun dispatch(context: Context) {
            val request = OneTimeWorkRequestBuilder<WidgetsUpdateWork>().build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork("WidgetsUpdateWork", ExistingWorkPolicy.APPEND_OR_REPLACE, request)
        }
    }

    override suspend fun doWork(): Result {
        GlanceWidget().updateAll(context)
        return Result.success()
    }
}