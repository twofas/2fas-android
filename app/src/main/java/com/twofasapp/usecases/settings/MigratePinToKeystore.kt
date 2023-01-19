package com.twofasapp.usecases.settings

import com.twofasapp.prefs.model.CheckLockStatus
import com.twofasapp.prefs.model.LockMethodEntity
import com.twofasapp.base.usecase.UseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single

class MigratePinToKeystore(
    private val getPinCode: GetPinCode,
    private val savePinCode: SavePinCode,
    private val checkLockStatus: CheckLockStatus,
    private val pinCodePreference: com.twofasapp.prefs.usecase.PinCodePreference,
    private val lockMethodPreference: com.twofasapp.prefs.usecase.LockMethodPreference,
) : UseCase<Completable> {

    override fun execute(subscribeScheduler: Scheduler, observeScheduler: Scheduler) =
        Single.just(checkLockStatus.execute())
            .filter { it == LockMethodEntity.PIN_LOCK }
            .flatMapSingle { getPinCode.execute() }
            .doOnSuccess {
                pinCodePreference.delete()
                lockMethodPreference.put(LockMethodEntity.PIN_SECURED)
            }
            .flatMapCompletable { savePinCode.execute(it) }
            .onErrorComplete()
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
}