package com.twofasapp.security.domain

import com.twofasapp.security.data.SecurityRepository
import com.twofasapp.security.domain.model.LockMethod

internal class GetLockMethodCaseImpl(
    private val securityRepository: SecurityRepository,
) : GetLockMethodCase {
    override fun invoke(): LockMethod {
        return securityRepository.getLockMethod()
    }
}