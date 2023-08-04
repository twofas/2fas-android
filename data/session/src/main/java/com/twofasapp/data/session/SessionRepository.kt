package com.twofasapp.data.session

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun isOnboardingDisplayed(): Boolean
    suspend fun showBackupReminder(): Boolean
    fun showAppUpdate(): Boolean
    fun setAppUpdateDisplayed()
    suspend fun setOnboardingDisplayed(isDisplayed: Boolean)
    suspend fun setRateAppDisplayed(isDisplayed: Boolean)
    fun observeShowBackupReminder(): Flow<Boolean>
    fun observeBackupEnabled(): Flow<Boolean>
    fun resetBackupReminder()
    suspend fun getAppInstallTimestamp(): Long
    suspend fun markAppInstalled()
}