package com.twofasapp.security.data.converter

import com.twofasapp.prefs.model.InvalidPinStatusEntity
import com.twofasapp.prefs.model.LockMethodEntity
import com.twofasapp.prefs.model.PinOptionsEntity
import com.twofasapp.security.domain.model.*

internal fun PinOptions.toEntity() = PinOptionsEntity(
    digits = digits.value,
    trials = trials.trials,
    timeout = timeout.timeoutMs,
)

internal fun PinOptionsEntity.toDomain() = PinOptions(
    digits = PinDigits.values().first { it.value == digits },
    trials = PinTrials.values().first { it.trials == trials },
    timeout = PinTimeout.values().first { it.timeoutMs == timeout },
)

internal fun LockMethod.toEntity(): LockMethodEntity = when (this) {
    LockMethod.NoLock -> LockMethodEntity.NO_LOCK
    LockMethod.Pin -> LockMethodEntity.PIN_SECURED
    LockMethod.Biometrics -> LockMethodEntity.FINGERPRINT_WITH_PIN_SECURED
}

internal fun LockMethodEntity.toDomain(): LockMethod = when (this) {
    LockMethodEntity.NO_LOCK -> LockMethod.NoLock
    LockMethodEntity.PIN_LOCK -> LockMethod.Pin
    LockMethodEntity.FINGERPRINT_LOCK -> LockMethod.Biometrics
    LockMethodEntity.PIN_SECURED -> LockMethod.Pin
    LockMethodEntity.FINGERPRINT_WITH_PIN_SECURED -> LockMethod.Biometrics
}

internal fun InvalidPinStatus.toEntity() = InvalidPinStatusEntity(
    attempts = attempts,
    lastAttemptSinceBootMs = lastAttemptSinceBootMs,
)