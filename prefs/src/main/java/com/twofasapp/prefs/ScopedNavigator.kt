package com.twofasapp.prefs

interface ScopedNavigator {
    fun openAuthenticate(canGoBack: Boolean = false, requestCode: Int? = null)
}