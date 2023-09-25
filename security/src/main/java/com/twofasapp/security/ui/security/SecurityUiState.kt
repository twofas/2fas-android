package com.twofasapp.security.ui.security

import com.twofasapp.data.session.domain.LockMethod
import com.twofasapp.data.session.domain.PinDigits
import com.twofasapp.data.session.domain.PinTimeout
import com.twofasapp.data.session.domain.PinTrials

internal data class SecurityUiState(
    val lockMethod: LockMethod = LockMethod.NoLock,
    val pinTrials: PinTrials = PinTrials.Trials3,
    val pinTimeout: PinTimeout = PinTimeout.Timeout5,
    val pinDigits: PinDigits = PinDigits.Code4,
    val allowScreenshots: Boolean = false,
)