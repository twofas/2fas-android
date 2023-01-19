package com.twofasapp.push.domain

import com.twofasapp.push.domain.model.Push
import kotlinx.coroutines.flow.Flow

interface ObserveInAppPushesCase {
    operator fun invoke(): Flow<Push>
}