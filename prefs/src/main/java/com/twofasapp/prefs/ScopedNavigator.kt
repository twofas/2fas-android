package com.twofasapp.prefs

import android.app.Activity

interface ScopedNavigator {
    fun requireActivity(): Activity
    fun navigateBack()
    fun finish()
    fun finishResultOk(params: Map<String, Any> = emptyMap())

    fun openAuthenticate(canGoBack: Boolean = false, requestCode: Int? = null)
}