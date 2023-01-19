package com.twofasapp.push.data

import com.twofasapp.push.domain.model.Push
import kotlinx.coroutines.flow.Flow

internal interface PushLocalData {
    fun observeInAppPushes(): Flow<Push>
    fun observeNotificationPushes(): Flow<Push>
    fun dispatchInAppPush(push: Push): Boolean
    fun dispatchNotificationPush(push: Push): Boolean
}
