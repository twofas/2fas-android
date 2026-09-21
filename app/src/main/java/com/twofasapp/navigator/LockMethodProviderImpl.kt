package com.twofasapp.navigator

import com.twofasapp.base.LockMethodProvider
import com.twofasapp.data.session.SecurityRepository
import com.twofasapp.data.session.domain.LockMethod

class LockMethodProviderImpl(
    private val securityRepository: SecurityRepository,
) : LockMethodProvider {
    override fun isNoLock(): Boolean {
        return securityRepository.getLockMethod() == LockMethod.NoLock
    }
}