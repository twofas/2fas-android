package com.twofasapp.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.twofasapp.App
import com.twofasapp.android.navigation.DeeplinkHandler
import com.twofasapp.base.AuthTracker
import com.twofasapp.base.lifecycle.AuthAware
import com.twofasapp.base.lifecycle.AuthLifecycle
import com.twofasapp.core.design.AppTheme
import com.twofasapp.core.design.ktx.applyAppTheme
import com.twofasapp.core.design.ktx.enableThemedEdgeToEdge
import com.twofasapp.data.session.CustomizationRepository
import com.twofasapp.ui.error.ErrorScreen
import com.twofasapp.workmanager.OnAppStartWork
import com.twofasapp.workmanager.OnAppUpdatedWorkDispatcher
import com.twofasapp.workmanager.SyncTimeWorkDispatcher
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import kotlin.system.exitProcess

class StartActivity : AppCompatActivity(), AuthAware {

    private val customizationRepository: CustomizationRepository by inject()
    private val authTracker: AuthTracker by inject()
    private val deeplinkHandler: DeeplinkHandler by inject()
    private val onAppUpdatedWorkDispatcher: OnAppUpdatedWorkDispatcher by inject()
    private val syncTimeWorkDispatcher: SyncTimeWorkDispatcher by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        App.migrationError?.let { error ->
            super.onCreate(savedInstanceState)
            setContent {
                AppTheme {
                    ErrorScreen(
                        title = "Couldn't start 2FAS",
                        message = "Something went wrong while upgrading your local data, so the app " +
                                "can't open safely. Your 2FA tokens have not been deleted. Please close " +
                                "the app and open it again to try once more. If it keeps happening, " +
                                "contact support with the details below and we'll help.",
                        details = error.stackTraceToString(),
                        onClose = { closeApp() },
                    )
                }
            }
            return
        }

        val selectedTheme = customizationRepository.getSelectedTheme()
        applyAppTheme(selectedTheme)
        enableThemedEdgeToEdge(theme = selectedTheme)
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
                authAware = this as? AuthAware,
            ),
        )
    }

    override fun onAuthenticated() {
        intent?.data?.let {
            deeplinkHandler.setQueuedDeeplink(incomingData = it.toString())
        }

        startActivity(
            Intent(this, MainActivity::class.java).apply {
                data = intent.data
            },
        )

        finish()
    }

    private fun closeApp() {
        finishAndRemoveTask()
        exitProcess(0)
    }
}