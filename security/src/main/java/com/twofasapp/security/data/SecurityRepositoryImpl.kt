package com.twofasapp.security.data

import com.twofasapp.common.time.TimeProvider
import com.twofasapp.prefs.usecase.InvalidPinStatusPreference
import com.twofasapp.prefs.usecase.LockMethodPreference
import com.twofasapp.prefs.usecase.PinOptionsPreference
import com.twofasapp.prefs.usecase.PinSecuredPreference
import com.twofasapp.security.data.converter.toDomain
import com.twofasapp.security.data.converter.toEntity
import com.twofasapp.security.domain.model.InvalidPinStatus
import com.twofasapp.security.domain.model.LockMethod
import com.twofasapp.security.domain.model.PinOptions
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
        return pinOptionsPreference.flow().map { it.toDomain() }
    }

    override suspend fun editPinOptions(pinOptions: PinOptions) {
        pinOptionsPreference.put(pinOptions.toEntity())
    }

    override suspend fun getPin(): String {
        return pinSecuredPreference.get()
    }

    override suspend fun editPin(pin: String) {
        pinSecuredPreference.put(pin)
    }

    override fun observeLockMethod(): Flow<LockMethod> {
        return lockMethodPreference.flow().map { it.toDomain() }
    }

    override fun getLockMethod(): LockMethod {
        return lockMethodPreference.get().toDomain()
    }

    override suspend fun editLockMethod(lockMethod: LockMethod) {
        lockMethodPreference.put(lockMethod.toEntity())
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
        invalidPinStatusPreference.put(invalidPinStatus.toEntity())
    }
}