package com.twofasapp.push.domain

import com.twofasapp.push.domain.model.Push
import kotlinx.coroutines.flow.Flow

internal interface ObserveNotificationPushesCase {
    operator fun invoke(): Flow<Push>
}