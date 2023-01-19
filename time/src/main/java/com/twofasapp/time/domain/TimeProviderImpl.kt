package com.twofasapp.time.domain

import android.os.SystemClock
import com.twofasapp.prefs.usecase.TimeDeltaPreference
import java.time.OffsetDateTime
import java.time.ZoneOffset

internal class TimeProviderImpl(
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

    override fun currentDateTimeUtc(): OffsetDateTime {
        return OffsetDateTime.now(ZoneOffset.UTC)
    }
}