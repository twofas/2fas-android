package com.twofasapp.feature.widget.sync

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.twofasapp.feature.widget.GlanceWidget
import org.koin.core.component.KoinComponent

class ToggleServiceWork(
    val context: Context,
    val params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {



    override suspend fun doWork(): Result {
        val serviceId = params.inputData.getLong("serviceId", -1L)


        GlanceWidget().updateAll(context)

//        delay(1000)
//
//        val request = OneTimeWorkRequestBuilder<DataSyncWorker>().build()
//
//        WorkManager.getInstance(context)
//            .enqueueUniqueWork("Test", ExistingWorkPolicy.APPEND_OR_REPLACE, request)
        return Result.success()
    }
}