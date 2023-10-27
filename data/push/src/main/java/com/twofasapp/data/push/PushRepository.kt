package com.twofasapp.data.push

import com.twofasapp.data.push.domain.Push
import kotlinx.coroutines.flow.Flow

internal interface PushRepository {
    fun observeInAppPushes(): Flow<Push>
    fun observeNotificationPushes(): Flow<Push>
    fun dispatchPush(push: Push)
}
