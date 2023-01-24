package com.twofasapp.data.session

import com.twofasapp.data.session.local.SessionLocalSource

internal class SessionRepositoryImpl(
    private val local: SessionLocalSource,
) : SessionRepository {

    override suspend fun isOnboardingDisplayed(): Boolean {
        return local.isOnboardingDisplayed()
    }

    override suspend fun setOnboardingDisplayed(isDisplayed: Boolean) {
        local.setOnboardingDisplayed(isDisplayed)
    }

    override suspend fun setRateAppDisplayed(isDisplayed: Boolean) {

    }
}