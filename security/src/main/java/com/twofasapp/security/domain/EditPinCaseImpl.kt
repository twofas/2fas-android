package com.twofasapp.security.domain

import com.twofasapp.security.data.SecurityRepository
import com.twofasapp.security.domain.model.LockMethod

internal class EditPinCaseImpl(
    private val securityRepository: SecurityRepository,
    private val editLockMethodCase: EditLockMethodCase,
    private val getLockMethodCase: GetLockMethodCase,
) : EditPinCase {

    override suspend fun invoke(pin: String) {
        val lockMethod = getLockMethodCase()
        securityRepository.editPin(pin)

        if (pin.isBlank()) {
            editLockMethodCase(LockMethod.NoLock)
        } else {
            when (lockMethod) {
                LockMethod.NoLock,
                LockMethod.Pin -> editLockMethodCase(LockMethod.Pin)
                LockMethod.Biometrics -> editLockMethodCase(LockMethod.Biometrics)
            }
        }
    }
}