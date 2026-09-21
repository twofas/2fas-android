package com.twofasapp.feature.security.ui.disablepin

import com.twofasapp.data.session.domain.PinDigits

internal data class DisablePinUiState(
    val digits: PinDigits = PinDigits.Code4,
    val enteredPin: String = "",
    val errorMessage: Int? = null,
    val verifying: Boolean = false,
    val invalidPinCount: Int = 0,
    val finished: Boolean = false,
)