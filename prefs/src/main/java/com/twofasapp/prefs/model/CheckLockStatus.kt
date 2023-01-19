package com.twofasapp.prefs.model

import com.twofasapp.prefs.usecase.LockMethodPreference

class CheckLockStatus(private val lockMethodPreference: LockMethodPreference) {

    fun execute() =
        lockMethodPreference.get()
}