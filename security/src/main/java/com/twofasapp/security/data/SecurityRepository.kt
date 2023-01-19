package com.twofasapp.security.data

import com.twofasapp.security.domain.model.InvalidPinStatus
import com.twofasapp.security.domain.model.LockMethod
import com.twofasapp.security.domain.model.PinOptions
import kotlinx.coroutines.flow.Flow

internal interface SecurityRepository {
    fun observePinOptions(): Flow<PinOptions>
    suspend fun editPinOptions(pinOptions: PinOptions)

    suspend fun getPin(): String
    suspend fun editPin(pin: String)

    fun observeLockMethod(): Flow<LockMethod>
    fun getLockMethod(): LockMethod
    suspend fun editLockMethod(lockMethod: LockMethod)

    fun observeInvalidPinStatus(): Flow<InvalidPinStatus>
    suspend fun editInvalidPinStatus(invalidPinStatus: InvalidPinStatus)
}