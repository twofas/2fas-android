package com.twofasapp.security.domain

import com.twofasapp.security.domain.model.LockMethod
import kotlinx.coroutines.flow.Flow


interface ObserveLockMethodCase {
    operator fun invoke(): Flow<LockMethod>
}