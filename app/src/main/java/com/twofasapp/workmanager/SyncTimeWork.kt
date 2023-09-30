package com.twofasapp.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.instacart.library.truetime.TrueTime
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.data.session.SessionRepository
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class SyncTimeWork(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {

    private val dispatchers: Dispatchers by inject()
    private val sessionRepository: SessionRepository by inject()

    override suspend fun doWork(): Result {
        return withContext(dispatchers.io) {
            try {
                listOf(
                    "time.google.com",
                    "time1.google.com",
                    "time.apple.com",
                    "pool.ntp.org",
                    "0.pool.ntp.org",
                    "1.pool.ntp.org",
                    "2.pool.ntp.org",
                ).random().let { ntpServer ->
                    TrueTime
                        .build()
                        .withNtpHost(ntpServer)
                        .initialize()

                    Timber.d("TrueTime: ${TrueTime.now()}")
                    sessionRepository.recalculateTimeDelta()
                    Result.success()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.success()
            }
        }
    }
}