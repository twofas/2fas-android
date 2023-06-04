package com.twofasapp.time.domain

import com.instacart.library.truetime.TrueTime
import com.instacart.library.truetime.TrueTimeRx
import com.twofasapp.prefs.usecase.TimeDeltaPreference
import kotlinx.coroutines.delay
import timber.log.Timber

class RecalculateTimeDeltaCaseImpl(
    private val timeDeltaPreference: TimeDeltaPreference,
) : RecalculateTimeDeltaCase {

    override suspend fun invoke() {
        var retries = 30

        while (recalculate().not() && retries > 0) {
            delay(2000)
            retries--
        }
    }

    private fun recalculate(): Boolean {
        Timber.d("TrueTime: sync...")
        return if (TrueTime.isInitialized()) {
            Timber.d("TrueTime: synced - ${TrueTimeRx.now()}")

//            val delta = timeDeltaPreference.get()
            val newDelta = TrueTimeRx.now().time - System.currentTimeMillis()

//            if (abs(newDelta - delta) > 1000) {
            timeDeltaPreference.put(newDelta)
//            }

            true
        } else {
            false
        }
    }
}