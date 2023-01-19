package com.twofasapp.security.domain

import com.twofasapp.security.domain.model.PinOptions
import kotlinx.coroutines.flow.Flow

interface ObservePinOptionsCase {
    operator fun invoke(): Flow<PinOptions>
}