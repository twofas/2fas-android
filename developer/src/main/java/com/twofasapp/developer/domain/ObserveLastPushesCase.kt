package com.twofasapp.developer.domain

import com.twofasapp.developer.domain.model.LastPush
import kotlinx.coroutines.flow.Flow

interface ObserveLastPushesCase {
    operator fun invoke(): Flow<List<LastPush>>
}