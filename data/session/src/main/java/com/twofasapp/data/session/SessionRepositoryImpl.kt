package com.twofasapp.data.session

import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.data.session.local.SessionLocalSource
import kotlinx.coroutines.withContext

internal class SessionRepositoryImpl(
    private val dispatchers: Dispatchers,
    private val local: SessionLocalSource,
) : SessionRepository {

    override suspend fun isOnboardingDisplayed(): Boolean {
        return withContext(dispatchers.io) {
            local.isOnboardingDisplayed()
        }
    }

    override suspend fun setOnboardingDisplayed(isDisplayed: Boolean) {
        withContext(dispatchers.io) {
            local.setOnboardingDisplayed(isDisplayed)
        }
    }

    override suspend fun setRateAppDisplayed(isDisplayed: Boolean) {

    }
}