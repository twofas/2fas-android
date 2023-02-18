package com.twofasapp.services.ui

import android.app.Activity

interface ServiceExternalNavigator {
    fun openSecurity(activity: Activity)
    fun openAuthenticate(activity: Activity)
}