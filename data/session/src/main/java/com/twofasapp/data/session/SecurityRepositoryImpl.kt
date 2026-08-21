package com.twofasapp.data.session

import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.common.storage.enumPref
import com.twofasapp.common.storage.serializedPref
import com.twofasapp.common.storage.stringPref
import com.twofasapp.common.time.TimeProvider
import com.twofasapp.data.session.domain.InvalidPinStatus
import com.twofasapp.data.session.domain.LockMethod
import com.twofasapp.data.session.domain.PinOptions
import com.twofasapp.data.session.mapper.asDomain
import com.twofasapp.data.session.mapper.asEntity
import com.twofasapp.prefs.model.InvalidPinStatusEntity
import com.twofasapp.prefs.model.LockMethodEntity
import com.twofasapp.prefs.model.PinOptionsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlin.math.ceil

internal class SecurityRepositoryImpl(
    dataStoreOwner: DataStoreOwner,
    private val timeProvider: TimeProvider,
) : SecurityRepository, DataStoreOwner by dataStoreOwner {

    private val lockMethod by enumPref(
        name = "lockMethod",
        default = LockMethodEntity.NoLock,
        cls = LockMethodEntity::class.java,
        encrypted = true,
    )

    private val invalidPinStatus by serializedPref(
        name = "invalidPinStatus",
        default = InvalidPinStatusEntity(),
        serializer = InvalidPinStatusEntity.serializer(),
        encrypted = true,
    )

    private val pinOptions by serializedPref(
        name = "pinOptions",
        default = PinOptionsEntity(digits = 4, trials = 3, timeout = 5 * 60 * 1000),
        serializer = PinOptionsEntity.serializer(),
        encrypted = true,
    )

    private val pin by stringPref(
        name = "pinSecured",
        default = "",
        encrypted = true,
    )

    override fun observePinOptions(): Flow<PinOptions> {
        return pinOptions.asFlow().map { it.asDomain() }
    }

    override suspend fun editPinOptions(pinOptions: PinOptions) {
        this.pinOptions.set(pinOptions.asEntity())
    }

    override suspend fun getPin(): String {
        return pin.get()
    }

    override suspend fun editPin(pin: String) {
        val lockMethod = getLockMethod()
        this.pin.set(pin)

        if (pin.isBlank()) {
            editLockMethod(LockMethod.NoLock)
        } else {
            when (lockMethod) {
                LockMethod.NoLock,
                LockMethod.Pin,
                -> editLockMethod(LockMethod.Pin)

                LockMethod.Biometrics -> editLockMethod(LockMethod.Biometrics)
            }
        }
    }

    override fun observeLockMethod(): Flow<LockMethod> {
        return lockMethod.asFlow().map { it.asDomain() }
    }

    override fun getLockMethod(): LockMethod {
        return runBlocking { lockMethod.get().asDomain() } // TODO: Migrate to suspend
    }

    override suspend fun editLockMethod(lockMethod: LockMethod) {
        this.lockMethod.set(lockMethod.asEntity())
    }

    override fun observeInvalidPinStatus(): Flow<InvalidPinStatus> {
        return invalidPinStatus.asFlow().map {
            val pinOptions = pinOptions.get()
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
                timeLeftMin = ceil(timeLeftMs / 1000f / 60f).toInt(),
            )
        }
    }

    override suspend fun editInvalidPinStatus(invalidPinStatus: InvalidPinStatus) {
        this.invalidPinStatus.set(invalidPinStatus.asEntity())
    }
}