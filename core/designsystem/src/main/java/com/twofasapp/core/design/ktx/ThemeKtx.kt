package com.twofasapp.core.design.ktx

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.twofasapp.common.domain.SelectedTheme

fun Context.applyAppTheme(theme: SelectedTheme) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getSystemService(UiModeManager::class.java)
            .setApplicationNightMode(
                when (theme) {
                    SelectedTheme.Light -> UiModeManager.MODE_NIGHT_NO
                    SelectedTheme.Dark -> UiModeManager.MODE_NIGHT_YES
                    SelectedTheme.Auto -> UiModeManager.MODE_NIGHT_AUTO
                },
            )
    } else {
        AppCompatDelegate.setDefaultNightMode(
            when (theme) {
                SelectedTheme.Light -> AppCompatDelegate.MODE_NIGHT_NO
                SelectedTheme.Dark -> AppCompatDelegate.MODE_NIGHT_YES
                SelectedTheme.Auto -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            },
        )
    }
}

fun ComponentActivity.enableThemedEdgeToEdge(theme: SelectedTheme) {
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.auto(
            lightScrim = Color.Transparent.toArgb(),
            darkScrim = Color.Transparent.toArgb(),
            detectDarkMode = { theme.isDark(it) },
        ),
        navigationBarStyle = SystemBarStyle.auto(
            lightScrim = Color.Transparent.toArgb(),
            darkScrim = Color.Transparent.toArgb(),
            detectDarkMode = { theme.isDark(it) },
        ),
    )
}

private fun SelectedTheme.isDark(resources: Resources): Boolean =
    when (this) {
        SelectedTheme.Auto -> (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        SelectedTheme.Light -> false
        SelectedTheme.Dark -> true
    }