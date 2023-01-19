package com.twofasapp.time.domain.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.twofasapp.time.domain.SyncTimeCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncTimeWork(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {

    private val syncTimeCase: SyncTimeCase by inject()

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            syncTimeCase.invoke()
            Result.success()
        }
    }
}