package com.twofasapp.time

import android.os.SystemClock
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.prefs.usecase.TimeDeltaPreference

class TimeProviderImpl(
    private val timeDeltaPreference: TimeDeltaPreference,
) : TimeProvider {

    override fun systemCurrentTime(): Long {
        return System.currentTimeMillis()
    }

    override fun systemElapsedTime(): Long {
        return SystemClock.elapsedRealtime()
    }

    override fun realCurrentTime(): Long {
        return systemCurrentTime() + realTimeDelta()
    }

    override fun realTimeDelta(): Long {
        return timeDeltaPreference.get()
    }
}