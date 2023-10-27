package com.twofasapp.data.session.domain

data class PinOptions(
    val digits: PinDigits,
    val trials: PinTrials,
    val timeout: PinTimeout,
) {
    companion object {
        val Default = PinOptions(
            PinDigits.Code4,
            PinTrials.Trials3,
            PinTimeout.Timeout5,
        )
    }
}