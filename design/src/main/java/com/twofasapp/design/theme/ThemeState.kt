package com.twofasapp.design.theme

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.twofasapp.prefs.model.AppTheme

object ThemeState {

    var appTheme by mutableStateOf(AppTheme.AUTO)

    fun applyTheme(theme: AppTheme) {
        val mode = when (theme) {
            AppTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            AppTheme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            AppTheme.AUTO -> {
                when {
                    Build.VERSION.SDK_INT >= 28 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    else -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                }
            }
        }

        appTheme = theme

        AppCompatDelegate.setDefaultNightMode(mode)
    }
}