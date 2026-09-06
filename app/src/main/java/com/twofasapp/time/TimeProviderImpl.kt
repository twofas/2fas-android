package com.twofasapp.time

import android.os.SystemClock
import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.common.storage.longPref
import com.twofasapp.common.time.TimeProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking

class TimeProviderImpl(
    dataStoreOwner: DataStoreOwner,
    dispatchers: Dispatchers,
) : TimeProvider, DataStoreOwner by dataStoreOwner {

    private val scope = CoroutineScope(dispatchers.io)

    private val timeDelta by longPref(
        name = "timeDelta",
        default = 0L,
    )

    private val timeDeltaCached: StateFlow<Long> =
        timeDelta.asFlow().stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = runBlocking { timeDelta.get() },
        )

    override fun systemCurrentTime(): Long {
        return System.currentTimeMillis()
    }

    override fun systemElapsedTime(): Long {
        return SystemClock.elapsedRealtime()
    }

    override fun realCurrentTime(): Long {
        return systemCurrentTime() + timeDeltaCached.value
    }
}