package com.twofasapp.usecases.rateapp

import com.twofasapp.BuildConfig
import com.twofasapp.base.usecase.UseCase
import io.reactivex.Scheduler
import java.time.Duration
import java.time.Instant

class RateAppCondition(private val rateAppStatusPreference: com.twofasapp.prefs.usecase.RateAppStatusPreference) : UseCase<Boolean> {

    companion object {
        val COUNTER_THRESHOLD = if (BuildConfig.DEBUG) 3 else 5
        private val DURATION_REACHED_COUNTER_THRESHOLD = if (BuildConfig.DEBUG) Duration.ofMinutes(5) else Duration.ofDays(7)
        private val DURATION_THRESHOLD = if (BuildConfig.DEBUG) Duration.ofMinutes(10) else Duration.ofDays(14)
    }

    override fun execute(subscribeScheduler: Scheduler, observeScheduler: Scheduler): Boolean {
        val status = rateAppStatusPreference.get()

        if (status.neverShowAgain) {
            return false
        }

        if (isCounterConditionMet(status)) {
            return true
        }

        if (isDurationConditionMet(status)) {
            return true
        }

        return false
    }

    private fun isCounterConditionMet(status: com.twofasapp.prefs.model.RateAppStatus) =
        status.counter >= COUNTER_THRESHOLD && Instant.now().isAfter(status.counterReached.plus(DURATION_REACHED_COUNTER_THRESHOLD))

    private fun isDurationConditionMet(status: com.twofasapp.prefs.model.RateAppStatus) =
        Instant.now().isAfter(status.counterStarted.plus(DURATION_THRESHOLD))
}