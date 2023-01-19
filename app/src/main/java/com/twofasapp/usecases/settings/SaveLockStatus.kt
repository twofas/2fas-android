package com.twofasapp.usecases.settings

import com.twofasapp.prefs.model.LockMethodEntity
import com.twofasapp.base.AuthTracker
import com.twofasapp.base.usecase.UseCaseParameterized
import io.reactivex.Scheduler

class SaveLockStatus(
    private val lockMethodPreference: com.twofasapp.prefs.usecase.LockMethodPreference,
    private val authTracker: AuthTracker
) : UseCaseParameterized<LockMethodEntity, Unit> {

    override fun execute(params: LockMethodEntity, subscribeScheduler: Scheduler, observeScheduler: Scheduler) {
        authTracker.onChangingLockStatus()
        lockMethodPreference.put(params)
    }
}