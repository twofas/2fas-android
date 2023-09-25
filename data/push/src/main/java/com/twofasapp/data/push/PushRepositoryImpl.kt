package com.twofasapp.data.push

import com.twofasapp.data.push.domain.Push
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

internal class PushRepositoryImpl : PushRepository {

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

    override fun dispatchPush(push: Push) {
        when (push.handler) {
            Push.Handler.InAppOnly -> dispatchInAppPush(push)
            Push.Handler.NotificationOnly -> dispatchNotificationPush(push)
            Push.Handler.InAppOrNotification -> {

                val dispatchSuccess = dispatchInAppPush(push)
                if (dispatchSuccess.not()) {
                    dispatchNotificationPush(push)
                }
            }
        }
    }

    private fun dispatchInAppPush(push: Push): Boolean {
        return if (flowInAppPushes.subscriptionCount.value == 0) {
            false
        } else {
            flowInAppPushes.tryEmit(push)
            true
        }
    }

    private fun dispatchNotificationPush(push: Push): Boolean {
        flowNotificationPushes.tryEmit(push)
        return true
    }
}