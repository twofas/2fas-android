package com.twofasapp.data.session.local

import com.twofasapp.storage.PlainPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.Instant

internal class SessionLocalSource(private val preferences: PlainPreferences) {

    companion object {
        private const val KeyShowOnboardWarning = "showOnboardWarning"
        private const val KeyBackupReminderTimestamp = "backupReminderTimestamp"
        private const val KeyAppInstallTimestamp = "appInstallTimestamp"
        private const val KeyNoCompanionAppFromTimestamp = "noCompanionAppFromTimestamp"
    }

    private val backupReminderTimestampFlow: MutableStateFlow<Long> by lazy {
        MutableStateFlow(getBackupReminderTimestamp())
    }

    fun isOnboardingDisplayed(): Boolean {
        return preferences.getBoolean(KeyShowOnboardWarning)?.not() ?: false
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

    fun getAppInstallTimestamp(): Long {
        return preferences.getLong(KeyAppInstallTimestamp) ?: Instant.now().toEpochMilli()
    }

    fun markAppInstalled() {
        if (preferences.getLong(KeyAppInstallTimestamp) == null) {
            preferences.putLong(KeyAppInstallTimestamp, Instant.now().toEpochMilli())
        }
    }

    fun getNoCompanionAppFromTimestamp(): Long? {
        return preferences.getLong(KeyNoCompanionAppFromTimestamp)
    }

    fun setNoCompanionAppFromTimestamp(millis: Long?) {
        if (millis == null) {
            preferences.delete(KeyNoCompanionAppFromTimestamp)
        } else {
            preferences.putLong(KeyNoCompanionAppFromTimestamp, millis)
        }
    }
}