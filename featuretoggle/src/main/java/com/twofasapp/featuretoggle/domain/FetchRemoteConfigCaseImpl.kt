package com.twofasapp.featuretoggle.domain

import com.twofasapp.featuretoggle.domain.repository.RemoteConfigRepository

internal class FetchRemoteConfigCaseImpl(
    private val remoteConfigRepository: RemoteConfigRepository
) : FetchRemoteConfigCase {

    override fun execute() {
        remoteConfigRepository.fetchAndActivate()
    }
}