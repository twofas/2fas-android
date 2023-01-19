package com.twofasapp.push.data

import com.twofasapp.push.domain.model.Push
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

internal class PushLocalDataImpl : PushLocalData {

    private val flowInAppPushes: MutableSharedFlow<Push> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val flowNotificationPushes: MutableSharedFlow<Push> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override fun observeInAppPushes(): Flow<Push> {
        return flowInAppPushes
    }

    override fun observeNotificationPushes(): Flow<Push> {
        return flowNotificationPushes
    }

    override fun dispatchInAppPush(push: Push): Boolean {
        return if (flowInAppPushes.subscriptionCount.value == 0) {
            false
        } else {
            flowInAppPushes.tryEmit(push)
            true
        }
    }

    override fun dispatchNotificationPush(push: Push): Boolean {
        flowNotificationPushes.tryEmit(push)
        return true
    }
}