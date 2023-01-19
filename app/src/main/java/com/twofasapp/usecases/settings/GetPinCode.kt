package com.twofasapp.usecases.settings

import com.twofasapp.prefs.model.CheckLockStatus
import com.twofasapp.prefs.model.LockMethodEntity
import com.twofasapp.base.usecase.UseCase
import io.reactivex.Scheduler
import io.reactivex.Single

class GetPinCode(
    private val pinCodePreference: com.twofasapp.prefs.usecase.PinCodePreference,
    private val pinSecuredPreference: com.twofasapp.prefs.usecase.PinSecuredPreference,
    private val checkLockStatus: CheckLockStatus
) : UseCase<Single<String>> {

    override fun execute(subscribeScheduler: Scheduler, observeScheduler: Scheduler) =
        Single.fromCallable {
            when (checkLockStatus.execute()) {
                LockMethodEntity.PIN_LOCK -> pinCodePreference.get()
                LockMethodEntity.PIN_SECURED,
                LockMethodEntity.FINGERPRINT_WITH_PIN_SECURED -> pinSecuredPreference.get()
                else -> ""
            }
        }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
}