package com.twofasapp.data.session.local

import com.twofasapp.storage.PlainPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class SessionLocalSource(private val preferences: PlainPreferences) {

    companion object {
        private const val KeyShowOnboardWarning = "showOnboardWarning"
        private const val KeyBackupReminderTimestamp = "backupReminderTimestamp"
    }

    private val backupReminderTimestampFlow: MutableStateFlow<Long> by lazy {
        MutableStateFlow(getBackupReminderTimestamp())
    }

    fun isOnboardingDisplayed(): Boolean {
        return preferences.getBoolean(KeyShowOnboardWarning)?.not() ?: true
    }

    fun setOnboardingDisplayed(isDisplayed: Boolean) {
        preferences.putBoolean(KeyShowOnboardWarning, isDisplayed.not())
    }

    fun observeBackupReminderTimestamp(): Flow<Long> {
        return backupReminderTimestampFlow
    }

    fun getBackupReminderTimestamp(): Long {
        return preferences.getLong(KeyBackupReminderTimestamp) ?: 0L
    }

    fun setBackupReminderTimestamp(millis: Long) {
        backupReminderTimestampFlow.update { millis }
        preferences.putLong(KeyBackupReminderTimestamp, millis)
    }
}