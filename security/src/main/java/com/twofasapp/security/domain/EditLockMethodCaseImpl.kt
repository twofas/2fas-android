package com.twofasapp.security.domain

import com.twofasapp.security.data.SecurityRepository
import com.twofasapp.security.domain.model.LockMethod

internal class EditLockMethodCaseImpl(
    private val securityRepository: SecurityRepository,
) : EditLockMethodCase {

    override suspend fun invoke(lockMethod: LockMethod) {
        return securityRepository.editLockMethod(lockMethod)
    }
}