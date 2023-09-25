package com.twofasapp.data.session

import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.session.domain.InvalidPinStatus
import com.twofasapp.data.session.domain.LockMethod
import com.twofasapp.data.session.domain.PinOptions
import com.twofasapp.data.session.mapper.asDomain
import com.twofasapp.data.session.mapper.asEntity
import com.twofasapp.prefs.usecase.InvalidPinStatusPreference
import com.twofasapp.prefs.usecase.LockMethodPreference
import com.twofasapp.prefs.usecase.PinOptionsPreference
import com.twofasapp.prefs.usecase.PinSecuredPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.ceil

internal class SecurityRepositoryImpl(
    private val pinOptionsPreference: PinOptionsPreference,
    private val pinSecuredPreference: PinSecuredPreference,
    private val lockMethodPreference: LockMethodPreference,
    private val invalidPinStatusPreference: InvalidPinStatusPreference,
    private val timeProvider: TimeProvider,
) : SecurityRepository {

    override fun observePinOptions(): Flow<PinOptions> {
        return pinOptionsPreference.flow().map { it.asDomain() }
    }

    override suspend fun editPinOptions(pinOptions: PinOptions) {
        pinOptionsPreference.put(pinOptions.asEntity())
    }

    override suspend fun getPin(): String {
        return pinSecuredPreference.get()
    }

    override suspend fun editPin(pin: String) {
        val lockMethod = getLockMethod()
        pinSecuredPreference.put(pin)

        if (pin.isBlank()) {
            editLockMethod(LockMethod.NoLock)
        } else {
            when (lockMethod) {
                LockMethod.NoLock,
                LockMethod.Pin -> editLockMethod(LockMethod.Pin)

                LockMethod.Biometrics -> editLockMethod(LockMethod.Biometrics)
            }
        }
    }

    override fun observeLockMethod(): Flow<LockMethod> {
        return lockMethodPreference.flow().map { it.asDomain() }
    }

    override fun getLockMethod(): LockMethod {
        return lockMethodPreference.get().asDomain()
    }

    override suspend fun editLockMethod(lockMethod: LockMethod) {
        lockMethodPreference.put(lockMethod.asEntity())
    }

    override fun observeInvalidPinStatus(): Flow<InvalidPinStatus> {
        val pinOptions = pinOptionsPreference.get()

        return invalidPinStatusPreference.flow().map {
            val timeLeftMs = it.lastAttemptSinceBootMs + pinOptions.timeout - timeProvider.systemElapsedTime()

            InvalidPinStatus(
                attempts = it.attempts,
                lastAttemptSinceBootMs = it.lastAttemptSinceBootMs,
                shouldBlock = when {
                    pinOptions.trials <= 0 -> false
                    it.attempts < pinOptions.trials -> false
                    timeProvider.systemElapsedTime() < it.lastAttemptSinceBootMs -> false // User restarted device
                    timeProvider.systemElapsedTime() - it.lastAttemptSinceBootMs > pinOptions.timeout -> false // Timeout elapsed
                    else -> true
                },
                timeLeftMs = timeLeftMs,
                timeLeftMin = ceil(timeLeftMs / 1000f / 60f).toInt()
            )
        }
    }

    override suspend fun editInvalidPinStatus(invalidPinStatus: InvalidPinStatus) {
        invalidPinStatusPreference.put(invalidPinStatus.asEntity())
    }
}