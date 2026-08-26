package com.twofasapp.core.design.window

import android.app.Activity
import com.twofasapp.common.domain.SelectedTheme
import com.twofasapp.core.design.AppThemeState
import com.twofasapp.core.design.ktx.makeWindowSecure

object ActivityHelper {
    fun onCreate(
        activity: Activity,
        selectedTheme: SelectedTheme,
        allowScreenshots: Boolean,
    ) {
        AppThemeState.applyTheme(selectedTheme)
        activity.makeWindowSecure(allow = allowScreenshots)
    }
}