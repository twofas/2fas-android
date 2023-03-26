package com.twofasapp.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.twofasapp.data.session.domain.SelectedTheme
import com.twofasapp.designsystem.AppTheme
import com.twofasapp.designsystem.LocalAppTheme
import com.twofasapp.designsystem.MainAppTheme
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.feature.home.navigation.HomeGraph
import com.twofasapp.feature.startup.navigation.StartupGraph
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val navController: NavHostController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (uiState.startDestination != null && uiState.selectedTheme != null) {
        CompositionLocalProvider(
            LocalAppTheme provides when (uiState.selectedTheme!!) {
                SelectedTheme.Auto -> AppTheme.Auto
                SelectedTheme.Light -> AppTheme.Light
                SelectedTheme.Dark -> AppTheme.Dark
            },
        ) {
            MainAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = TwTheme.color.background,
                ) {
                    val startDestination = when (uiState.startDestination!!) {
                        MainUiState.StartDestination.Onboarding -> StartupGraph.route
                        MainUiState.StartDestination.Home -> HomeGraph.route
                    }

                    MainNavHost(navController, startDestination = startDestination)

                    if (uiState.browserExtRequests.isNotEmpty()) {
                        val browserExtRequest = uiState.browserExtRequests.first()
                        BrowserExtRequestDialog(
                            browserExtRequest = browserExtRequest,
                            onRequestHandled = {
                                viewModel.browserExtRequestHandled(browserExtRequest)
                                NotificationManagerCompat.from(context).cancel(null, browserExtRequest.request.requestId.hashCode())
                            }
                        )
                    }
                }
            }
        }
    }
}