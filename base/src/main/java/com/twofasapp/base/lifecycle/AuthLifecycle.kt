package com.twofasapp.base.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.twofasapp.base.AuthTracker
import com.twofasapp.base.AuthenticationStatus
import com.twofasapp.prefs.ScopedNavigator

class AuthLifecycle(
    private val authTracker: AuthTracker,
    private val navigator: ScopedNavigator?,
    private val authAware: AuthAware?,
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        when (authTracker.shouldAuthenticate()) {
            AuthenticationStatus.Valid -> authAware?.onAuthenticated()
            AuthenticationStatus.Expired -> navigator?.openAuthenticate()
        }
    }
}