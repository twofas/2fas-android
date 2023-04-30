package com.twofasapp.usecases.rateapp

import com.twofasapp.common.time.TimeProvider
import com.twofasapp.prefs.usecase.RateAppStatusPreference
import com.twofasapp.usecases.rateapp.RateAppCondition.Companion.COUNTER_THRESHOLD
import java.time.Instant

class UpdateRateAppStatus(
    private val rateAppStatusPreference: RateAppStatusPreference,
    private val timeProvider: TimeProvider,
) {

    @Deprecated("Old approach")
    fun markNeverShowAgain() {
        save(rateAppStatusPreference.get().copy(neverShowAgain = true))
    }

    @Deprecated("Old approach")
    fun markDismissed() {
        save(rateAppStatusPreference.get().copy(counter = COUNTER_THRESHOLD, counterStarted = Instant.now(), counterReached = Instant.now()))
    }

    fun markShown() {
        save(rateAppStatusPreference.get().copy(counter = COUNTER_THRESHOLD, counterStarted = Instant.now(), counterReached = Instant.now()))
    }

    fun incrementCounter() {
        val currentStatus = rateAppStatusPreference.get()

        if (currentStatus.counter == COUNTER_THRESHOLD) {
            return
        }

        val newCounter = currentStatus.counter + 1
        val isCounterReached = newCounter == COUNTER_THRESHOLD
        val counterReached = if (isCounterReached) Instant.now() else currentStatus.counterReached

        save(currentStatus.copy(counter = newCounter, counterReached = counterReached))
    }

    private fun save(rateAppStatus: com.twofasapp.prefs.model.RateAppStatus) {
        rateAppStatusPreference.put(rateAppStatus)
    }
}