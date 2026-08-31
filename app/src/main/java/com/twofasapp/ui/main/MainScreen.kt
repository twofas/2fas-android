package com.twofasapp.ui.main

import android.Manifest
import android.os.Build
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.android.navigation.Screen
import com.twofasapp.common.domain.SelectedTheme
import com.twofasapp.core.design.AppTheme
import com.twofasapp.core.design.LocalAppTheme
import com.twofasapp.core.design.LocalDynamicColors
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.feature.permissions.RequestPermission
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun MainScreen(
    viewModel: MainViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    uiState.events.firstOrNull()?.let {
        viewModel.consumeEvent(it)
    }

    uiState.selectedTheme?.let { selectedTheme ->
        CompositionLocalProvider(
            LocalAppTheme provides when (selectedTheme) {
                SelectedTheme.Auto -> AppTheme.Auto
                SelectedTheme.Light -> AppTheme.Light
                SelectedTheme.Dark -> AppTheme.Dark
            },
            LocalDynamicColors provides uiState.dynamicColors,
        ) {
            AppTheme {
                if (uiState.startDestination != null) {
                    Surface(
                        color = MdtTheme.color.background,
                    ) {
                        val startDestination = when (uiState.startDestination!!) {
                            MainUiState.StartDestination.Onboarding -> Screen.Startup
                            MainUiState.StartDestination.Home -> Screen.Home
                        }

                        MainNavDisplay(
                            startDestination = startDestination,
                            showBackupError = uiState.showBackupError,
                            onServiceAddedSuccessfully = { viewModel.serviceAdded(it) },
                        )
                    }

                    if (uiState.browserExtRequests.isNotEmpty()) {
                        val browserExtRequest = uiState.browserExtRequests.first()
                        BrowserExtRequestDialog(
                            browserExtRequest = browserExtRequest,
                            onRequestHandled = {
                                viewModel.browserExtRequestHandled(browserExtRequest)
                                NotificationManagerCompat.from(context).cancel(null, browserExtRequest.request.requestId.hashCode())
                            },
                        )
                    }
                }
            }
        }
    }

    // TODO: Remove
    var askForPushPermission by remember { mutableStateOf(true) }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (askForPushPermission) {
            RequestPermission(
                permission = Manifest.permission.POST_NOTIFICATIONS,
                rationaleEnabled = false,
                onGranted = {
                    askForPushPermission = false
                },
                onDenied = {
                    askForPushPermission = false
                },
                onDismissRequest = {
                    askForPushPermission = false
                },
            )
        }
    }
}