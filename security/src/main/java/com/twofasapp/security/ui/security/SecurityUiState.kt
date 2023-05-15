package com.twofasapp.security.ui.security

import com.twofasapp.security.domain.model.LockMethod
import com.twofasapp.security.domain.model.PinDigits
import com.twofasapp.security.domain.model.PinTimeout
import com.twofasapp.security.domain.model.PinTrials

internal data class SecurityUiState(
    val lockMethod: LockMethod = LockMethod.NoLock,
    val pinTrials: PinTrials = PinTrials.Trials3,
    val pinTimeout: PinTimeout = PinTimeout.Timeout5,
    val pinDigits: PinDigits = PinDigits.Code4,
    val allowScreenshots: Boolean = false,
)