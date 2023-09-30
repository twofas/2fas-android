package com.twofasapp.migration

import com.twofasapp.prefs.model.LockMethodEntity
import com.twofasapp.prefs.usecase.LockMethodPreference
import com.twofasapp.prefs.usecase.PinCodePreference
import com.twofasapp.prefs.usecase.PinSecuredPreference

class MigratePin(
    private val lockMethodPreference: LockMethodPreference,
    private val pinCodePreference: PinCodePreference,
    private val pinSecuredPreference: PinSecuredPreference,
) {
    suspend fun invoke() {
        if (lockMethodPreference.get() == LockMethodEntity.PIN_LOCK) {
            lockMethodPreference.put(LockMethodEntity.PIN_SECURED)

            pinSecuredPreference.put(pinCodePreference.get())
            pinCodePreference.delete()
        }
    }
}