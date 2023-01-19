package com.twofasapp.push.domain

import com.twofasapp.push.domain.model.Push
import com.twofasapp.push.domain.repository.PushRepository

internal class PushDispatchCaseImpl(
    private val pushRepository: PushRepository,
) : PushDispatchCase {

    override operator fun invoke(push: Push) {
        pushRepository.dispatchPush(push)
    }
}