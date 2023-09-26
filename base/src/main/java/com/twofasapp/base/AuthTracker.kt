package com.twofasapp.base

import com.twofasapp.prefs.model.CheckLockStatus
import com.twofasapp.prefs.model.LockMethodEntity
import java.time.Instant
import javax.inject.Provider

class AuthTracker(
    private val checkLockStatus: Provider<CheckLockStatus>
) {

    companion object {
        private const val VALIDITY_TIME_MS = 30 * 1000L
    }

    private var lastBackgroundTime: Instant = Instant.MIN
    private var lastForegroundTime: Instant = Instant.now()
    private var isAuthenticated = false

    fun onAppCreate() {
        reset()
    }

    fun onSplashScreen() {
        reset()
    }

    fun onAuthenticateScreen() {
        reset()
    }

    fun onWidgetSettingsScreen() {
        reset()
    }


    fun onChangingLockStatus() {
        isAuthenticated = true
    }

    fun onMovingToBackground() {
        if (isAuthenticated) {
            lastBackgroundTime = Instant.now()
            isAuthenticated = false
        }
    }

    fun onMovingToForeground() {
        lastForegroundTime = Instant.now()

        if (isValidityTimeElapsed().not()) {
            isAuthenticated = true
        }
    }

    fun onAuthenticated() {
        isAuthenticated = true
    }

    fun shouldAuthenticate(): AuthenticationStatus {
        return when {
            isNoLock() -> {
                AuthenticationStatus.Valid
            }
            isSessionStillAuthenticated() -> {
                AuthenticationStatus.Valid
            }
            isValidityTimeElapsed() -> {
                AuthenticationStatus.Expired
            }
            else -> {
                AuthenticationStatus.Valid
            }
        }
    }

    private fun isSessionStillAuthenticated() = isAuthenticated

    private fun isNoLock() = checkLockStatus.get().execute() == LockMethodEntity.NO_LOCK

    private fun isValidityTimeElapsed() = lastForegroundTime.minusMillis(VALIDITY_TIME_MS).isAfter(lastBackgroundTime)

    private fun reset() {
        lastBackgroundTime = Instant.MIN
        lastForegroundTime = Instant.now()
        isAuthenticated = false
    }
}