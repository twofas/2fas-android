package com.twofasapp.ui.main

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.twofasapp.android.navigation.LegacyScreen
import com.twofasapp.android.navigation.Modal
import com.twofasapp.android.navigation.NavAnimation
import com.twofasapp.android.navigation.NavArg
import com.twofasapp.android.navigation.intentFor
import com.twofasapp.common.ktx.legacyEncodeBase64ToString
import com.twofasapp.core.design.foundation.modal.ModalBottomSheet
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.feature.home.navigation.HomeNavigationListener
import com.twofasapp.feature.home.navigation.homeNavigation
import com.twofasapp.feature.home.ui.services.add.AddServiceModal
import com.twofasapp.feature.home.ui.services.focus.FocusServiceModal
import com.twofasapp.feature.home.ui.services.focus.FocusServiceModalNavArg
import com.twofasapp.feature.security.ui.lock.LockActivity
import com.twofasapp.feature.startup.navigation.StartupRoute
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalMaterialApi::class)
@Composable
internal fun MainNavHost(
    navController: NavHostController,
    bottomSheetNavigator: BottomSheetNavigator,
    bottomSheetState: ModalBottomSheetState,
    startDestination: String,
    onServiceAddedSuccessfully: (RecentlyAddedService) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var authSuccessCallback: () -> Unit = {}
    val startAuthForResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                authSuccessCallback()
            }
        }

    var recentlyAddedService by remember { mutableStateOf<RecentlyAddedService?>(null) }

    BackHandler(enabled = bottomSheetNavigator.navigatorSheetState.isVisible) {
        scope.launch {
            navController.popBackStack()
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            if (recentlyAddedService != null) {
                onServiceAddedSuccessfully.invoke(recentlyAddedService!!)
                recentlyAddedService = null
            }
        },
        bottomSheetNavigator = bottomSheetNavigator,
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = NavAnimation.Enter,
            exitTransition = NavAnimation.Exit,
        ) {
            composable(LegacyScreen.Startup.route) {
                StartupRoute()
            }

            homeNavigation(
                navController = navController,
                listener = object : HomeNavigationListener {
                    override fun openService(activity: Activity, serviceId: Long) {
                        navController.navigate(LegacyScreen.EditService.routeWithArgs(NavArg.ServiceId to serviceId))
                    }

                    override fun openExternalImport() {
                        navController.navigate(LegacyScreen.ExternalImportSelector.route)
                    }

                    override fun openBrowserExt() {
                        navController.navigate(LegacyScreen.BrowserExt.route)
                    }

                    override fun openSecurity(activity: Activity) {
                    }

                    override fun openBackup(turnOnBackup: Boolean) {
                        navController.navigate(LegacyScreen.Backup.routeWithArgs(NavArg.TurnOnBackup to turnOnBackup))
                    }

                    override fun openAppSettings() {
                    }

                    override fun openTrash() {
                    }

                    override fun openNotifications() {
                        navController.navigate(LegacyScreen.Notifications.route)
                    }

                    override fun openAbout() {
                    }

                    override fun openDeveloper() {
                    }

                    override fun openAddServiceModal() {
                        recentlyAddedService = null
                        navController.navigate(Modal.AddService.routeWithArgs())
                    }

                    override fun openFocusServiceModal(id: Long) {
                        navController.navigate(Modal.FocusService.route.replace("{id}", id.toString()))
                    }

                    override fun openBackupImport(filePath: String?) {
                        navController.navigate(LegacyScreen.BackupImport.routeWithArgs(NavArg.ImportFileUri to filePath?.legacyEncodeBase64ToString()))
                    }
                },
                openEditServiceAuth = { onSuccess ->
                    authSuccessCallback = onSuccess

                    startAuthForResult.launch(context.intentFor<LockActivity>("canGoBack" to true))
                },
            )

            bottomSheet(Modal.AddService.route, listOf(NavArg.AddServiceInitRoute)) {
                AddServiceModal(
                    initRoute = it.arguments?.getString(NavArg.AddServiceInitRoute.name),
                    onAddedSuccessfully = { recentlyAddedService = it },
                    openGuides = { navController.navigate(LegacyScreen.Guides.route) },
                )
            }

            bottomSheet(Modal.FocusService.route, listOf(FocusServiceModalNavArg.ServiceId)) {
                FocusServiceModal(
                    openService = {
                        navController.navigate(LegacyScreen.EditService.routeWithArgs(NavArg.ServiceId to it))
                        scope.launch { bottomSheetState.hide() }
                    },
                )
            }
        }
    }
}