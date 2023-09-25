package com.twofasapp.data.session.mapper

import com.twofasapp.data.session.domain.InvalidPinStatus
import com.twofasapp.data.session.domain.LockMethod
import com.twofasapp.data.session.domain.PinDigits
import com.twofasapp.data.session.domain.PinOptions
import com.twofasapp.data.session.domain.PinTimeout
import com.twofasapp.data.session.domain.PinTrials
import com.twofasapp.prefs.model.InvalidPinStatusEntity
import com.twofasapp.prefs.model.LockMethodEntity
import com.twofasapp.prefs.model.PinOptionsEntity

internal fun PinOptions.asEntity() = PinOptionsEntity(
    digits = digits.value,
    trials = trials.trials,
    timeout = timeout.timeoutMs,
)

internal fun PinOptionsEntity.asDomain() = PinOptions(
    digits = PinDigits.entries.first { it.value == digits },
    trials = PinTrials.entries.first { it.trials == trials },
    timeout = PinTimeout.entries.first { it.timeoutMs == timeout },
)

internal fun LockMethod.asEntity(): LockMethodEntity = when (this) {
    LockMethod.NoLock -> LockMethodEntity.NO_LOCK
    LockMethod.Pin -> LockMethodEntity.PIN_SECURED
    LockMethod.Biometrics -> LockMethodEntity.FINGERPRINT_WITH_PIN_SECURED
}

internal fun LockMethodEntity.asDomain(): LockMethod = when (this) {
    LockMethodEntity.NO_LOCK -> LockMethod.NoLock
    LockMethodEntity.PIN_LOCK -> LockMethod.Pin
    LockMethodEntity.FINGERPRINT_LOCK -> LockMethod.Biometrics
    LockMethodEntity.PIN_SECURED -> LockMethod.Pin
    LockMethodEntity.FINGERPRINT_WITH_PIN_SECURED -> LockMethod.Biometrics
}

internal fun InvalidPinStatus.asEntity() = InvalidPinStatusEntity(
    attempts = attempts,
    lastAttemptSinceBootMs = lastAttemptSinceBootMs,
)