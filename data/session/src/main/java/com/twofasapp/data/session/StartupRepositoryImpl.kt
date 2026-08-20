package com.twofasapp.data.session

import com.twofasapp.common.coroutines.Dispatchers
import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.common.storage.booleanPref
import kotlinx.coroutines.withContext

internal class StartupRepositoryImpl(
    dataStoreOwner: DataStoreOwner,
    private val dispatchers: Dispatchers,
) : StartupRepository, DataStoreOwner by dataStoreOwner {

    private val onboardingDisplayed by booleanPref(name = "onboardingDisplayed", default = false)

    override suspend fun isOnboardingDisplayed(): Boolean {
        return withContext(dispatchers.io) { onboardingDisplayed.get() }
    }

    override suspend fun setOnboardingDisplayed(isDisplayed: Boolean) {
        withContext(dispatchers.io) { onboardingDisplayed.set(isDisplayed) }
    }
}