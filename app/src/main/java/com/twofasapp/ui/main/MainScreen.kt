package com.twofasapp.ui.main

import android.Manifest
import android.os.Build
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.twofasapp.RequestPermission
import com.twofasapp.android.navigation.LegacyScreen
import com.twofasapp.android.navigation.Screen
import com.twofasapp.common.domain.SelectedTheme
import com.twofasapp.core.design.AppTheme
import com.twofasapp.core.design.LocalAppTheme
import com.twofasapp.core.design.LocalDynamicColors
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.data.services.domain.RecentlyAddedService
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

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
                    val useNavigation3 = true

                    Surface(
                        color = MdtTheme.color.background,
                    ) {
                        if (useNavigation3) {
                            val startDestination = when (uiState.startDestination!!) {
                                MainUiState.StartDestination.Onboarding -> Screen.Startup
                                MainUiState.StartDestination.Home -> Screen.Services
                            }

                            MainNavDisplay(
                                startDestination = startDestination,
                                onServiceAddedSuccessfully = { viewModel.serviceAdded(it) },
                            )
                        } else {
                            LegacyMainNavHost(
                                uiState = uiState,
                                onToggleAdvanceExpanded = { viewModel.toggleAdvanceExpanded() },
                                onServiceAddedSuccessfully = { viewModel.serviceAdded(it) },
                            )
                        }
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

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalMaterialApi::class)
@Composable
private fun LegacyMainNavHost(
    uiState: MainUiState,
    onToggleAdvanceExpanded: () -> Unit,
    onServiceAddedSuccessfully: (RecentlyAddedService) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = {
            when (it) {
                ModalBottomSheetValue.Hidden -> {
                    if (uiState.addServiceAdvancedExpanded) {
                        onToggleAdvanceExpanded()
                        false
                    } else {
                        true
                    }
                }

                ModalBottomSheetValue.Expanded -> true
                ModalBottomSheetValue.HalfExpanded -> true
            }
        },
        skipHalfExpanded = true,
    )
    val bottomSheetNavigator = remember { BottomSheetNavigator(sheetState) }
    val navController = rememberNavController(bottomSheetNavigator)

    LaunchedEffect(Unit) {
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

    val startDestination = when (uiState.startDestination!!) {
        MainUiState.StartDestination.Onboarding -> LegacyScreen.Startup.route
        MainUiState.StartDestination.Home -> LegacyScreen.Services.route
    }

    MainNavHost(
        navController = navController,
        bottomSheetNavigator = bottomSheetNavigator,
        bottomSheetState = sheetState,
        startDestination = startDestination,
        onServiceAddedSuccessfully = onServiceAddedSuccessfully,
    )
}