package com.twofasapp.security.domain

import com.twofasapp.security.domain.model.InvalidPinStatus
import kotlinx.coroutines.flow.Flow

interface ObserveInvalidPinStatusCase {
    operator fun invoke(): Flow<InvalidPinStatus>
}