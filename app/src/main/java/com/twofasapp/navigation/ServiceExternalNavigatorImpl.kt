package com.twofasapp.navigation

import android.app.Activity
import com.twofasapp.extensions.startActivity
import com.twofasapp.extensions.startActivityForResult
import com.twofasapp.security.ui.lock.LockActivity
import com.twofasapp.security.ui.security.SecurityActivity
import com.twofasapp.services.ui.ServiceActivity
import com.twofasapp.services.ui.ServiceExternalNavigator

class ServiceExternalNavigatorImpl : ServiceExternalNavigator {
    override fun openSecurity(activity: Activity) {
        activity.startActivity<SecurityActivity>()
    }

    override fun openAuthenticate(activity: Activity) {
        activity.startActivityForResult<LockActivity>(
            ServiceActivity.REQUEST_KEY_AUTH_SERVICE, "canGoBack" to true
        )
    }
}