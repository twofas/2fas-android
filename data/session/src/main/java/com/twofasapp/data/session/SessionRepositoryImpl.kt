package com.twofasapp.data.session

import com.twofasapp.data.session.local.SessionLocalSource

class SessionRepositoryImpl(
    private val localSource: SessionLocalSource,
) : SessionRepository {

    override suspend fun isOnboardingDisplayed(): Boolean {
        return localSource.isOnboardingDisplayed()
    }

    override suspend fun setOnboardingDisplayed(isDisplayed: Boolean) {
        localSource.setOnboardingDisplayed(isDisplayed)
    }
}