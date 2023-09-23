package com.twofasapp.design.theme

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.twofasapp.common.domain.SelectedTheme

object ThemeState {

    fun applyTheme(theme: com.twofasapp.common.domain.SelectedTheme) {
        val mode = when (theme) {
            com.twofasapp.common.domain.SelectedTheme.Light -> AppCompatDelegate.MODE_NIGHT_NO
            com.twofasapp.common.domain.SelectedTheme.Dark -> AppCompatDelegate.MODE_NIGHT_YES
            com.twofasapp.common.domain.SelectedTheme.Auto -> {
                when {
                    Build.VERSION.SDK_INT >= 28 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    else -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                }
            }
        }

        AppCompatDelegate.setDefaultNightMode(mode)
    }
}