package com.twofasapp.security.domain

import com.twofasapp.security.domain.model.LockMethod


interface GetLockMethodCase {
    operator fun invoke(): LockMethod
}