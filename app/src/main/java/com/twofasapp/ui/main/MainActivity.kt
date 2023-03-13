package com.twofasapp.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.design.theme.ThemeState
import com.twofasapp.prefs.usecase.AppThemePreference
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val settingsRepository: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeState.applyTheme(settingsRepository.getAppSettings().selectedTheme)

        super.onCreate(savedInstanceState)
        installSplashScreen()

        setContent { MainScreen() }
    }
}