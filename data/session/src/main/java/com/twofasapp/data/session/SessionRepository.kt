package com.twofasapp.data.session

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun showAppUpdate(): Boolean
    suspend fun setAppUpdateDisplayed()
    suspend fun setRateAppDisplayed(isDisplayed: Boolean)
    suspend fun getAppInstallTimestamp(): Long
    suspend fun markAppInstalled()
    suspend fun recalculateTimeDelta()
    suspend fun noCompanionAppFromTimestamp(): Long?
    fun observeShowPassBanner(): Flow<Boolean>
    suspend fun resetPassBannerDismiss()
    suspend fun disablePassBanner()
    fun observeAppReviewPrompted(): Flow<Boolean>
    suspend fun markAppReviewPrompted()
}