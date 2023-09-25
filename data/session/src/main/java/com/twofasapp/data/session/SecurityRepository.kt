package com.twofasapp.data.session

import com.twofasapp.data.session.domain.InvalidPinStatus
import com.twofasapp.data.session.domain.LockMethod
import com.twofasapp.data.session.domain.PinOptions
import kotlinx.coroutines.flow.Flow

interface SecurityRepository {
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