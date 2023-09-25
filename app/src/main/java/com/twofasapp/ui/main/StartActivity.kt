package com.twofasapp.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.twofasapp.DeeplinkHandler
import com.twofasapp.base.AuthTracker
import com.twofasapp.base.lifecycle.AuthAware
import com.twofasapp.base.lifecycle.AuthLifecycle
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.designsystem.AppThemeState
import com.twofasapp.workmanager.SyncTimeWorkDispatcher
import com.twofasapp.workmanager.OnAppUpdatedWorkDispatcher
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
        AppThemeState.applyTheme(settingsRepository.getAppSettings().selectedTheme)
        super.onCreate(savedInstanceState)

        installSplashScreen()

        onAppUpdatedWorkDispatcher.dispatch()
        syncTimeWorkDispatcher.dispatch()

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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let {
            deeplinkHandler.setQueuedDeeplink(incomingData = it.toString())
        }
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