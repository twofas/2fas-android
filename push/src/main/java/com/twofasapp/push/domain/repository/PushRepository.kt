package com.twofasapp.push.domain.repository

import com.twofasapp.push.domain.model.Push
import kotlinx.coroutines.flow.Flow

internal interface PushRepository {
    fun observeInAppPushes(): Flow<Push>
    fun observeNotificationPushes(): Flow<Push>
    fun dispatchPush(push: Push)
}
