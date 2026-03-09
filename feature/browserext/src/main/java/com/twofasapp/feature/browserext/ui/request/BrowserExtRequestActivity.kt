package com.twofasapp.feature.browserext.ui.request

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.twofasapp.base.AuthTracker
import com.twofasapp.base.lifecycle.AuthAware
import com.twofasapp.base.lifecycle.AuthLifecycle
import com.twofasapp.common.domain.SelectedTheme
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.designsystem.AppTheme
import com.twofasapp.designsystem.LocalAppTheme
import com.twofasapp.designsystem.LocalDynamicColors
import com.twofasapp.designsystem.MainAppTheme
import com.twofasapp.designsystem.activity.ActivityHelper
import com.twofasapp.feature.browserext.notification.BrowserExtRequestPayload
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class BrowserExtRequestActivity : ComponentActivity(), AuthAware {

    private val settingsRepository: SettingsRepository by inject()
    private val authTracker: AuthTracker by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityHelper.onCreate(
            activity = this,
            selectedTheme = settingsRepository.getAppSettings().selectedTheme,
            allowScreenshots = settingsRepository.getAppSettings().allowScreenshots,
        )
        super.onCreate(savedInstanceState)

        val payload = intent.getParcelableExtra<BrowserExtRequestPayload>(BrowserExtRequestPayload.Key)!!

        authTracker.onBrowserExtRequest()

        lifecycle.addObserver(
            AuthLifecycle(
                authTracker = get(),
                navigator = get { parametersOf(this) },
                authAware = this as? AuthAware
            )
        )

        setContent {
            CompositionLocalProvider(
                LocalAppTheme provides when (settingsRepository.getAppSettings().selectedTheme) {
                    SelectedTheme.Auto -> AppTheme.Auto
                    SelectedTheme.Light -> AppTheme.Light
                    SelectedTheme.Dark -> AppTheme.Dark
                },
                LocalDynamicColors provides settingsRepository.getAppSettings().dynamicColors,
            ) {
                MainAppTheme {
                    BrowserExtRequestScreen(payload = payload)
                }
            }
        }
    }

    override fun onAuthenticated() = Unit
}