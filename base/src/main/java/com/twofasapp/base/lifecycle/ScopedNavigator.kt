package com.twofasapp.base.lifecycle

interface ScopedNavigator {
    fun openAuthenticate(canGoBack: Boolean = false, requestCode: Int? = null)
}