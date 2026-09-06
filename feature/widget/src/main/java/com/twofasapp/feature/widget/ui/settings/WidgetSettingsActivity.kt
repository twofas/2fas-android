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
import com.twofasapp.core.design.ktx.applyAppTheme
import com.twofasapp.core.design.ktx.enableThemedEdgeToEdge
import com.twofasapp.data.session.CustomizationRepository
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class WidgetSettingsActivity : ComponentActivity(), AuthAware {

    private val customizationRepository: CustomizationRepository by inject()
    private val authTracker: AuthTracker by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val selectedTheme = customizationRepository.getSelectedTheme()
        val dynamicColors = customizationRepository.getDynamicColors()
        applyAppTheme(selectedTheme)
        enableThemedEdgeToEdge(theme = selectedTheme)

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
                LocalAppTheme provides when (selectedTheme) {
                    SelectedTheme.Auto -> AppTheme.Auto
                    SelectedTheme.Light -> AppTheme.Light
                    SelectedTheme.Dark -> AppTheme.Dark
                },
                LocalDynamicColors provides dynamicColors,
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