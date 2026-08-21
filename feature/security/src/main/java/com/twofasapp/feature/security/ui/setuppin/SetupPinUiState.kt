package com.twofasapp.feature.security.ui.setuppin

import com.twofasapp.data.session.domain.PinDigits
import com.twofasapp.locale.R

internal data class SetupPinUiState(
    val digits: PinDigits = PinDigits.Code4,
    val showPinOptions: Boolean = true,
    val enteredPin: String = "",
    val firstPin: String = "",
    val message: Int = R.string.security__enter_your_new_pin,
    val errorMessage: Int? = null,
    val verifying: Boolean = false,
    val invalidPinCount: Int = 0,
    val finished: Boolean = false,
)