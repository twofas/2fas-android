package com.twofasapp.feature.widget.ui.settings

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.twofasapp.base.AuthTracker
import com.twofasapp.base.lifecycle.AuthAware
import com.twofasapp.base.lifecycle.AuthLifecycle
import com.twofasapp.common.domain.SelectedTheme
import com.twofasapp.core.design.AppTheme
import com.twofasapp.core.design.LocalAppTheme
import com.twofasapp.core.design.LocalDynamicColors
import com.twofasapp.core.design.window.ActivityHelper
import com.twofasapp.data.session.SettingsRepository
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class WidgetSettingsActivity : ComponentActivity(), AuthAware {

    private val settingsRepository: SettingsRepository by inject()
    private val authTracker: AuthTracker by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityHelper.onCreate(
            activity = this,
            selectedTheme = settingsRepository.getAppSettings().selectedTheme,
            allowScreenshots = settingsRepository.getAppSettings().allowScreenshots,
        )
        super.onCreate(savedInstanceState)
        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        authTracker.onWidgetSettingsScreen()

        lifecycle.addObserver(
            AuthLifecycle(
                authTracker = get(),
                navigator = get { parametersOf(this) },
                authAware = this as? AuthAware,
            ),
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
                AppTheme {
                    WidgetSettingsScreen(
                        appWidgetId = appWidgetId,
                    ) {
                        setResult(Activity.RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId))
                        finishAndRemoveTask()
                    }
                }
            }
        }
    }

    override fun onAuthenticated() = Unit
}