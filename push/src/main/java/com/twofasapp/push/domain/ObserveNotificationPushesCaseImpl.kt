package com.twofasapp.push.domain

import com.twofasapp.push.domain.model.Push
import com.twofasapp.push.domain.repository.PushRepository
import kotlinx.coroutines.flow.Flow

internal class ObserveNotificationPushesCaseImpl(
    private val pushRepository: PushRepository,
) : ObserveNotificationPushesCase {

    override operator fun invoke(): Flow<Push> {
        return pushRepository.observeNotificationPushes()
    }
}