package com.twofasapp.feature.widget.sync

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.twofasapp.data.services.WidgetsRepository
import com.twofasapp.feature.widget.GlanceWidget
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.time.Duration

class WidgetsPeriodicRefreshWork(
    val context: Context,
    val params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {

    private val widgetsRepository: WidgetsRepository by inject()

    companion object {
        fun dispatch(context: Context) {
            Timber.d("Start worker")
            val request = OneTimeWorkRequestBuilder<WidgetsPeriodicRefreshWork>().build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork("WidgetsPeriodicRefreshWork", ExistingWorkPolicy.REPLACE, request)
        }
    }

    override suspend fun doWork(): Result {
        delay(1000)

        Timber.d("Update")
        widgetsRepository.incrementRefreshTicker()
        GlanceWidget().updateAll(context)

        val widgets = widgetsRepository.getWidgets()

        // Stop when all services are hidden
        if (widgets.list.flatMap { it.services }.all { it.revealed.not() }) {
            Timber.d("Stop: hidden")
            return Result.success()
        }

        // Stop after 5 seconds
        if (widgets.list.all { it.lastInteraction + Duration.ofSeconds(10).toMillis() < widgetsRepository.getRefreshTicker() }) {
            Timber.d("Stop: time elapsed - hide all")
            widgetsRepository.hideAll()
            return Result.success()
        }

        val request = OneTimeWorkRequestBuilder<WidgetsPeriodicRefreshWork>().build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork("WidgetsPeriodicRefreshWork", ExistingWorkPolicy.APPEND_OR_REPLACE, request)

        return Result.success()
    }
}