package com.twofasapp.feature.security.ui.changepin

import com.twofasapp.data.session.domain.PinDigits

internal data class ChangePinUiState(
    val digits: PinDigits = PinDigits.Code4,
    val enteredPin: String = "",
    val errorMessage: Int? = null,
    val verifying: Boolean = false,
    val invalidPinCount: Int = 0,
    val currentPinVerified: Boolean = false,
)