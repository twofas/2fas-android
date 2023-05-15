package com.twofasapp.data.session.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.twofasapp.data.session.SettingsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DisableScreenshotsWork(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val settingsRepository: SettingsRepository by inject()

    override suspend fun doWork(): Result {
        settingsRepository.setAllowScreenshots(false)
        return Result.success()
    }
}