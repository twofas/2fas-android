package com.twofasapp.time

import android.os.SystemClock
import com.twofasapp.common.time.TimeProvider
import java.time.OffsetDateTime
import java.time.ZoneOffset

class TimeProviderImpl : TimeProvider {

    override fun currentDateTimeUtc(): OffsetDateTime {
        return OffsetDateTime.now(ZoneOffset.UTC)
    }

    override fun systemCurrentTime(): Long {
        return System.currentTimeMillis()
    }

    override fun systemElapsedTime(): Long {
        return SystemClock.elapsedRealtime()
    }
}