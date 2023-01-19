package com.twofasapp.security.domain

import com.twofasapp.security.data.SecurityRepository
import com.twofasapp.security.domain.model.LockMethod
import kotlinx.coroutines.flow.Flow

internal class ObserveLockMethodCaseImpl(
    private val securityRepository: SecurityRepository,
) : ObserveLockMethodCase {

    override fun invoke(): Flow<LockMethod> {
        return securityRepository.observeLockMethod()
    }
}