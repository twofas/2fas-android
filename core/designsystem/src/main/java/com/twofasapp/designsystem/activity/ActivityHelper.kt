package com.twofasapp.designsystem.activity

import android.app.Activity
import com.twofasapp.common.domain.SelectedTheme
import com.twofasapp.designsystem.AppThemeState
import com.twofasapp.designsystem.ktx.makeWindowSecure

object ActivityHelper {
    fun onCreate(
        activity: Activity,
        selectedTheme: SelectedTheme,
        allowScreenshots: Boolean
    ) {
        AppThemeState.applyTheme(selectedTheme)
        activity.makeWindowSecure(allow = allowScreenshots)
    }
}