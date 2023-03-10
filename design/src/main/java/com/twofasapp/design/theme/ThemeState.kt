package com.twofasapp.design.theme

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.twofasapp.data.session.domain.SelectedTheme

object ThemeState {

    fun applyTheme(theme: SelectedTheme) {
        val mode = when (theme) {
            SelectedTheme.Light -> AppCompatDelegate.MODE_NIGHT_NO
            SelectedTheme.Dark -> AppCompatDelegate.MODE_NIGHT_YES
            SelectedTheme.Auto -> {
                when {
                    Build.VERSION.SDK_INT >= 28 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    else -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                }
            }
        }

        AppCompatDelegate.setDefaultNightMode(mode)
    }
}