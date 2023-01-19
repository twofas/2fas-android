package com.twofasapp.usecases.services

class PinOptionsUseCase(
    private val pinOptionsPreference: com.twofasapp.prefs.usecase.PinOptionsPreference
) {

    var tmpDigits: Int? = null

    fun get() = pinOptionsPreference.get()

    fun set(pinOptions: com.twofasapp.prefs.model.PinOptionsEntity) = pinOptionsPreference.put(pinOptions)

    fun editDigits(digits: Int) {
        set(get().copy(digits = digits))
    }

    fun editTrials(trials: Int) {
        set(get().copy(trials = trials))
    }

    fun editTimeout(timeout: Int) {
        set(get().copy(timeout = timeout))
    }
}