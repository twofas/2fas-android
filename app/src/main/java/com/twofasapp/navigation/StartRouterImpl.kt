package com.twofasapp.navigation

import android.app.Activity
import com.twofasapp.extensions.startActivity
import com.twofasapp.features.main.MainServicesActivity

class StartRouterImpl(
    private val activity: Activity
) : StartRouter() {

    override fun navigateBack() = activity.onBackPressed()

    override fun navigate(direction: StartDirections) {
        when (direction) {
            is StartDirections.Main -> activity.startActivity<MainServicesActivity>()
        }
    }
}