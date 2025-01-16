package com.twofasapp.ui.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.twofasapp.android.navigation.DeeplinkHandler
import com.twofasapp.base.AuthTracker
import com.twofasapp.base.lifecycle.AuthAware
import com.twofasapp.base.lifecycle.AuthLifecycle
import com.twofasapp.common.domain.SelectedTheme
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.designsystem.AppThemeState
import com.twofasapp.workmanager.OnAppStartWork
import com.twofasapp.workmanager.OnAppUpdatedWorkDispatcher
import com.twofasapp.workmanager.SyncTimeWorkDispatcher
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class StartActivity : AppCompatActivity(), AuthAware {

    private val settingsRepository: SettingsRepository by inject()
    private val authTracker: AuthTracker by inject()
    private val deeplinkHandler: DeeplinkHandler by inject()
    private val onAppUpdatedWorkDispatcher: OnAppUpdatedWorkDispatcher by inject()
    private val syncTimeWorkDispatcher: SyncTimeWorkDispatcher by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val selectedTheme = settingsRepository.getAppSettings().selectedTheme
        AppThemeState.applyTheme(selectedTheme)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = Color.Transparent.toArgb(),
                darkScrim = Color.Transparent.toArgb(),
                detectDarkMode = {
                    when (selectedTheme) {
                        SelectedTheme.Auto -> (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
                        SelectedTheme.Light -> false
                        SelectedTheme.Dark -> true
                    }
                }
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = Color.Transparent.toArgb(),
                darkScrim = Color.Transparent.toArgb(),
                detectDarkMode = {
                    when (selectedTheme) {
                        SelectedTheme.Auto -> (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
                        SelectedTheme.Light -> false
                        SelectedTheme.Dark -> true
                    }
                }
            ),
        )
        super.onCreate(savedInstanceState)

        installSplashScreen()

        onAppUpdatedWorkDispatcher.dispatch()
        syncTimeWorkDispatcher.dispatch()
        OnAppStartWork.dispatch(this)

        if (savedInstanceState == null) {
            authTracker.onSplashScreen()
        }

        lifecycle.addObserver(
            AuthLifecycle(
                authTracker = get(),
                navigator = get { parametersOf(this) },
                authAware = this as? AuthAware
            )
        )
    }

    override fun onAuthenticated() {
        intent?.data?.let {
            deeplinkHandler.setQueuedDeeplink(incomingData = it.toString())
        }

        startActivity(
            Intent(this, MainActivity::class.java).apply {
                data = intent.data
            }
        )

        finish()
    }
}