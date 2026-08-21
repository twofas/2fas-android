package com.twofasapp.core.design.window

import android.app.Activity
import com.twofasapp.common.domain.SelectedTheme
import com.twofasapp.core.design.ktx.applyAppTheme
import com.twofasapp.core.design.ktx.makeWindowSecure

object ActivityHelper {
    fun onCreate(
        activity: Activity,
        selectedTheme: SelectedTheme,
        allowScreenshots: Boolean,
    ) {
        activity.applyAppTheme(selectedTheme)
        activity.makeWindowSecure(allow = allowScreenshots)
    }
}