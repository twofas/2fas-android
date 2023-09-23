package com.twofasapp.feature.widget.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.twofasapp.data.services.WidgetsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WidgetsDeleteWork(
    val context: Context,
    val params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {

    private val widgetsRepository: WidgetsRepository by inject()

    companion object {
        fun dispatch(context: Context, appWidgetIds: List<Int>) {
            val request = OneTimeWorkRequestBuilder<WidgetsDeleteWork>()
                .setInputData(
                    Data.Builder().apply {
                        putIntArray("ids", appWidgetIds.toIntArray())
                    }.build()
                )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork("WidgetsDeleteWork", ExistingWorkPolicy.APPEND_OR_REPLACE, request)
        }
    }

    override suspend fun doWork(): Result {
        val ids = inputData.getIntArray("ids")
        widgetsRepository.deleteWidget(ids?.toList().orEmpty())
        return Result.success()
    }
}