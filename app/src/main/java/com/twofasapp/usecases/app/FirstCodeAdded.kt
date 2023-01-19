package com.twofasapp.usecases.app

import com.twofasapp.prefs.usecase.FirstCodeAddedPreference

class FirstCodeAdded(
    private val firstCodeAddedPreference: com.twofasapp.prefs.usecase.FirstCodeAddedPreference,
) {

    fun isAdded() = firstCodeAddedPreference.get()

    fun save(isAdded: Boolean) {
        firstCodeAddedPreference.put(isAdded)
    }
}