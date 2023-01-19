package com.twofasapp.featuretoggle.domain

import com.twofasapp.featuretoggle.domain.model.RemoteConfig
import com.twofasapp.featuretoggle.domain.repository.RemoteConfigRepository
import io.reactivex.Flowable
import io.reactivex.Scheduler

internal class ObserveRemoteConfigCaseImpl(
    private val remoteConfigRepository: RemoteConfigRepository,
) : ObserveRemoteConfigCase {

    override fun execute(subscribeScheduler: Scheduler, observeScheduler: Scheduler): Flowable<RemoteConfig> {
        return remoteConfigRepository.observe()
    }
}