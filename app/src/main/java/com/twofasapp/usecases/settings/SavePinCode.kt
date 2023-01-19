package com.twofasapp.usecases.settings

import com.twofasapp.prefs.usecase.PinSecuredPreference
import com.twofasapp.base.usecase.UseCaseParameterized
import io.reactivex.Completable
import io.reactivex.Scheduler

class SavePinCode(
    private val pinSecuredPreference: com.twofasapp.prefs.usecase.PinSecuredPreference,
) : UseCaseParameterized<String, Completable> {

    override fun execute(params: String, subscribeScheduler: Scheduler, observeScheduler: Scheduler) =
        Completable.fromCallable { pinSecuredPreference.put(params) }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
}