package com.twofasapp.push.domain.repository

import com.twofasapp.push.data.PushLocalData
import com.twofasapp.push.domain.model.Push
import kotlinx.coroutines.flow.Flow

internal class PushRepositoryImpl(
    private val pushLocalData: PushLocalData,
) : PushRepository {

    override fun observeInAppPushes(): Flow<Push> {
        return pushLocalData.observeInAppPushes()
    }

    override fun observeNotificationPushes(): Flow<Push> {
        return pushLocalData.observeNotificationPushes()
    }

    override fun dispatchPush(push: Push) {
        when (push.handler) {
            Push.Handler.InAppOnly -> pushLocalData.dispatchInAppPush(push)
            Push.Handler.NotificationOnly -> pushLocalData.dispatchNotificationPush(push)
            Push.Handler.InAppOrNotification -> {

                val dispatchSuccess = pushLocalData.dispatchInAppPush(push)
                if (dispatchSuccess.not()) {
                    pushLocalData.dispatchNotificationPush(push)
                }
            }
        }
    }
}