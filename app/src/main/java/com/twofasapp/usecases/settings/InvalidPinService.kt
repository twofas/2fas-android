package com.twofasapp.usecases.settings

import android.os.SystemClock
import com.twofasapp.prefs.model.InvalidPinStatusEntity
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class InvalidPinService(
    private val invalidPinStatusPreference: com.twofasapp.prefs.usecase.InvalidPinStatusPreference
) {

    fun getStatus(subscribeScheduler: Scheduler = Schedulers.io(), observeScheduler: Scheduler = AndroidSchedulers.mainThread()) =
        Single.fromCallable { invalidPinStatusPreference.get() }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)

    fun setStatus(
        status: InvalidPinStatusEntity,
        subscribeScheduler: Scheduler = Schedulers.io(),
        observeScheduler: Scheduler = AndroidSchedulers.mainThread()
    ) =
        Completable.fromCallable { invalidPinStatusPreference.put(status) }
            .toSingle { status }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)

    fun incrementAttempts() =
        getStatus().flatMap { setStatus(it.copy(attempts = it.attempts + 1, lastAttemptSinceBootMs = SystemClock.elapsedRealtime())) }

    fun reset() =
        getStatus().flatMap { setStatus(InvalidPinStatusEntity()) }
}