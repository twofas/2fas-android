package com.twofasapp.time.domain

import com.instacart.library.truetime.TrueTime
import com.instacart.library.truetime.TrueTimeRx
import com.twofasapp.prefs.usecase.TimeDeltaPreference
import kotlin.math.abs

class RecalculateTimeDeltaCaseImpl(
    private val timeDeltaPreference: TimeDeltaPreference,
) : RecalculateTimeDeltaCase {

    override fun invoke() {
        if (TrueTime.isInitialized()) {
            val delta = timeDeltaPreference.get()
            val newDelta = TrueTimeRx.now().time - System.currentTimeMillis()

            if (abs(newDelta - delta) > 1000) {
                timeDeltaPreference.put(newDelta)
            }
        }
    }
}