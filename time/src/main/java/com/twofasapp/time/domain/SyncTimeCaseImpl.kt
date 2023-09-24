package com.twofasapp.time.domain

import com.instacart.library.truetime.TrueTime
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.ktx.runSafely
import kotlinx.coroutines.withContext
import timber.log.Timber

class SyncTimeCaseImpl(
    private val dispatchers: Dispatchers,
    private val recalculateTimeDeltaCase: RecalculateTimeDeltaCase
) : SyncTimeCase {

    override suspend fun invoke() {
        withContext(dispatchers.io) {

            runSafely {
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
                    recalculateTimeDeltaCase.invoke()
                }
            }
                .onSuccess { Timber.d("TrueTime: Success") }
                .onFailure { Timber.d("TrueTime: Failure - ${it.message}") }

        }
    }
}