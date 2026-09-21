package com.twofasapp.feature.browserext.ui.request

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
import com.twofasapp.feature.browserext.notification.BrowserExtRequestPayload
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class BrowserExtRequestActivity : ComponentActivity(), AuthAware {

    private val customizationRepository: CustomizationRepository by inject()
    private val authTracker: AuthTracker by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val selectedTheme = customizationRepository.getSelectedTheme()
        val dynamicColors = customizationRepository.getDynamicColors()
        applyAppTheme(selectedTheme)
        enableThemedEdgeToEdge(theme = selectedTheme)
        super.onCreate(savedInstanceState)

        val payload = intent.getParcelableExtra<BrowserExtRequestPayload>(BrowserExtRequestPayload.Key)!!

        authTracker.onBrowserExtRequest()

        lifecycle.addObserver(
            AuthLifecycle(
                authTracker = get(),
                navigator = get { parametersOf(this) },
                authAware = this as? AuthAware,
            ),
        )

        setContent {
            CompositionLocalProvider(
                LocalAppTheme provides when (customizationRepository.getSelectedTheme()) {
                    SelectedTheme.Auto -> AppTheme.Auto
                    SelectedTheme.Light -> AppTheme.Light
                    SelectedTheme.Dark -> AppTheme.Dark
                },
                LocalDynamicColors provides dynamicColors,
            ) {
                AppTheme {
                    BrowserExtRequestScreen(payload = payload)
                }
            }
        }
    }

    override fun onAuthenticated() = Unit
}