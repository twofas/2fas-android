package com.twofasapp.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.twofasapp.data.session.domain.SelectedTheme
import com.twofasapp.designsystem.AppTheme
import com.twofasapp.designsystem.LocalAppTheme
import com.twofasapp.designsystem.MainAppTheme
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.feature.home.navigation.HomeGraph
import com.twofasapp.feature.startup.navigation.StartupGraph
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalMaterialApi::class)
@Composable
internal fun MainScreen(
    viewModel: MainViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = {
            when (it) {
                ModalBottomSheetValue.Hidden -> {
                    if (uiState.addServiceAdvancedExpanded) {
                        viewModel.toggleAdvanceExpanded()
                        false
                    } else {
                        true
                    }
                }

                ModalBottomSheetValue.Expanded -> true
                ModalBottomSheetValue.HalfExpanded -> true
            }
        },
        skipHalfExpanded = true
    )
    val bottomSheetNavigator = remember { BottomSheetNavigator(sheetState) }
    val navController: NavHostController = rememberNavController(bottomSheetNavigator)
    val context = LocalContext.current

    LaunchedEffect(Unit){
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            val argumentsLog: String = if (destination.arguments.isEmpty()) {
                ""
            } else {
               "args=" + destination.arguments.map {
                    @Suppress("DEPRECATION")
                    "${it.key}=${arguments?.get(it.key)}"
                }.toString()
            }

            Timber.tag("NavController").d("route=${destination.route}  $argumentsLog")
        }
    }

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

                    MainNavHost(
                        navController = navController,
                        bottomSheetNavigator = bottomSheetNavigator,
                        bottomSheetState = sheetState,
                        startDestination = startDestination,
                        onServiceAddedSuccessfully = { viewModel.serviceAdded(it) },
                    )

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