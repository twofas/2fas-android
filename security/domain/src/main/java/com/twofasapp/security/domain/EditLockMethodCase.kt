package com.twofasapp.security.domain

import com.twofasapp.security.domain.model.LockMethod

interface EditLockMethodCase {
    suspend operator fun invoke(lockMethod: LockMethod)
}