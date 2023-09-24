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
import com.twofasapp.android.navigation.Modal
import com.twofasapp.android.navigation.NavAnimation
import com.twofasapp.android.navigation.NavArg
import com.twofasapp.android.navigation.Screen
import com.twofasapp.android.navigation.intentFor
import com.twofasapp.android.navigation.withArg
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.designsystem.common.ModalBottomSheet
import com.twofasapp.feature.about.navigation.AboutGraph
import com.twofasapp.feature.about.navigation.aboutNavigation
import com.twofasapp.feature.appsettings.navigation.AppSettingsGraph
import com.twofasapp.feature.appsettings.navigation.appSettingsNavigation
import com.twofasapp.feature.backup.navigation.BackupExportRoute
import com.twofasapp.feature.backup.navigation.BackupImportRoute
import com.twofasapp.feature.backup.navigation.BackupRoute
import com.twofasapp.feature.backup.navigation.BackupSettingsRoute
import com.twofasapp.feature.browserext.navigation.BrowserExtDetailsRoute
import com.twofasapp.feature.browserext.navigation.BrowserExtPairingRoute
import com.twofasapp.feature.browserext.navigation.BrowserExtPermissionRoute
import com.twofasapp.feature.browserext.navigation.BrowserExtRoute
import com.twofasapp.feature.browserext.navigation.BrowserExtScanRoute
import com.twofasapp.feature.externalimport.domain.ImportType
import com.twofasapp.feature.externalimport.navigation.ExternalImportResultRoute
import com.twofasapp.feature.externalimport.navigation.ExternalImportRoute
import com.twofasapp.feature.externalimport.navigation.ExternalImportScanRoute
import com.twofasapp.feature.externalimport.navigation.ExternalImportSelectorRoute
import com.twofasapp.feature.home.navigation.GuideInitRoute
import com.twofasapp.feature.home.navigation.GuidePagerRoute
import com.twofasapp.feature.home.navigation.GuidesRoute
import com.twofasapp.feature.home.navigation.HomeGraph
import com.twofasapp.feature.home.navigation.HomeNavigationListener
import com.twofasapp.feature.home.navigation.HomeNode
import com.twofasapp.feature.home.navigation.NotificationsGraph
import com.twofasapp.feature.home.navigation.homeNavigation
import com.twofasapp.feature.home.navigation.notificationsNavigation
import com.twofasapp.feature.home.ui.services.add.AddServiceModal
import com.twofasapp.feature.home.ui.services.focus.FocusServiceModal
import com.twofasapp.feature.home.ui.services.focus.FocusServiceModalNavArg
import com.twofasapp.feature.startup.navigation.startupNavigation
import com.twofasapp.feature.trash.navigation.TrashGraph
import com.twofasapp.feature.trash.navigation.trashNavigation
import com.twofasapp.security.navigation.SecurityGraph
import com.twofasapp.security.navigation.securityNavigation
import com.twofasapp.security.ui.lock.LockActivity
import com.twofasapp.services.navigation.ServiceGraph
import com.twofasapp.services.navigation.ServiceNavArg
import com.twofasapp.services.navigation.serviceNavigation
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

            startupNavigation(
                openHome = { navController.navigate(HomeGraph.route) { popUpTo(0) } }
            )

            homeNavigation(
                navController = navController,
                listener = object : HomeNavigationListener {
                    override fun openService(activity: Activity, serviceId: Long) {
                        navController.navigate(ServiceGraph.route.withArg(ServiceNavArg.ServiceId, serviceId))
                    }

                    override fun openExternalImport() {
                        navController.navigate(Screen.ExternalImportSelector.route)
                    }

                    override fun openBrowserExt() {
                        navController.navigate(Screen.BrowserExt.route)
                    }

                    override fun openSecurity(activity: Activity) {
                        navController.navigate(SecurityGraph.route)
                    }

                    override fun openBackup() {
                        navController.navigate(Screen.Backup.route)
                    }

                    override fun openAppSettings() {
                        navController.navigate(AppSettingsGraph.route)
                    }

                    override fun openTrash() {
                        navController.navigate(TrashGraph.route)
                    }

                    override fun openNotifications() {
                        navController.navigate(NotificationsGraph.route)
                    }

                    override fun openAbout() {
                        navController.navigate(AboutGraph.route)
                    }

                    override fun openAddServiceModal() {
                        recentlyAddedService = null
                        navController.navigate(Modal.AddService.route)
                    }

                    override fun openFocusServiceModal(id: Long) {
                        navController.navigate(Modal.FocusService.route.replace("{id}", id.toString()))
                    }
                }
            )

            serviceNavigation(
                navController = navController,
                openSecurity = { navController.navigate(SecurityGraph.route) },
                openAuth = { onSuccess ->
                    authSuccessCallback = onSuccess

                    startAuthForResult.launch(context.intentFor<LockActivity>("canGoBack" to true))
                },
            )

            appSettingsNavigation()
            notificationsNavigation()
            trashNavigation(navController = navController)
            aboutNavigation(navController = navController)
            securityNavigation(navController = navController)

            bottomSheet(Modal.AddService.route, listOf(NavArg.AddServiceInitRoute)) {
                AddServiceModal(
                    initRoute = it.arguments?.getString(NavArg.AddServiceInitRoute.name),
                    onAddedSuccessfully = { recentlyAddedService = it },
                    openGuides = { navController.navigate(Screen.Guides.route) }
                )
            }

            bottomSheet(Modal.FocusService.route, listOf(FocusServiceModalNavArg.ServiceId)) {
                FocusServiceModal(
                    openService = {
                        navController.navigate(ServiceGraph.route.withArg(ServiceNavArg.ServiceId, it))
                        scope.launch { bottomSheetState.hide() }
                    }
                )
            }

            composable(Screen.Guides.route) {
                GuidesRoute(
                    openGuide = { navController.navigate(Screen.GuideInit.routeWithArgs(NavArg.Guide to it.name)) }
                )
            }

            composable(Screen.GuideInit.route, listOf(NavArg.Guide)) {
                GuideInitRoute(
                    guide = enumValueOf(it.arguments!!.getString(NavArg.Guide.name)!!),
                    openGuide = { guide, guideVariantIndex ->
                        navController.navigate(
                            Screen.GuidePager.routeWithArgs(
                                NavArg.Guide to guide.name,
                                NavArg.GuideVariantIndex to guideVariantIndex,
                            )
                        )
                    }
                )
            }

            composable(Screen.GuidePager.route, listOf(NavArg.Guide, NavArg.GuideVariantIndex)) {
                GuidePagerRoute(
                    guide = enumValueOf(it.arguments!!.getString(NavArg.Guide.name)!!),
                    guideVariantIndex = it.arguments!!.getInt(NavArg.GuideVariantIndex.name),
                    openAddScan = {
                        navController.popBackStack(HomeNode.Services.route, false)
                        navController.navigate(Modal.AddService.route)
                    },
                    openAddManually = {
                        navController.popBackStack(HomeNode.Services.route, false)
                        navController.navigate(Modal.AddService.routeWithArgs(NavArg.AddServiceInitRoute to "manual"))
                    },
                )
            }

            composable(Screen.Backup.route) {
                BackupRoute(
                    openSettings = { navController.navigate(Screen.BackupSettings.route) },
                    openExport = { navController.navigate(Screen.BackupExport.route) },
                    openImport = { navController.navigate(Screen.BackupImport.route) },
                )
            }

            composable(Screen.BackupSettings.route) {
                BackupSettingsRoute(
                    goBack = { navController.popBackStack() }
                )
            }

            composable(Screen.BackupExport.route) {
                BackupExportRoute(
                    goBack = { navController.popBackStack() }
                )
            }

            composable(Screen.BackupImport.route) {
                BackupImportRoute(
                    goBack = { navController.popBackStack() }
                )
            }

            composable(Screen.BrowserExt.route) {
                BrowserExtRoute(
                    openScan = { navController.navigate(Screen.BrowserExtScan.route) },
                    openDetails = { extensionId ->
                        navController.navigate(Screen.BrowserExtDetails.routeWithArgs(NavArg.ExtensionId to extensionId))
                    }
                )
            }

            composable(Screen.BrowserExtPermission.route) {
                BrowserExtPermissionRoute(
                    openMain = { navController.popBackStack(Screen.BrowserExt.route, false) }
                )
            }

            composable(Screen.BrowserExtScan.route) {
                BrowserExtScanRoute(
                    openProgress = { extensionId ->
                        navController.navigate(Screen.BrowserExtPairing.routeWithArgs(NavArg.ExtensionId to extensionId)) {
                            popUpTo(Screen.BrowserExt.route)
                        }
                    }
                )
            }

            composable(Screen.BrowserExtPairing.route, listOf(NavArg.ExtensionId)) {
                BrowserExtPairingRoute(
                    openMain = { navController.popBackStack(Screen.BrowserExt.route, false) },
                    openPermission = { navController.navigate(Screen.BrowserExtPermission.route) { popUpTo(Screen.BrowserExt.route) } },
                    openScan = { navController.navigate(Screen.BrowserExtScan.route) { popUpTo(Screen.BrowserExt.route) } }
                )
            }

            composable(Screen.BrowserExtDetails.route, listOf(NavArg.ExtensionId)) {
                BrowserExtDetailsRoute(
                    openMain = { navController.popBackStack(Screen.BrowserExt.route, false) },
                )
            }

            composable(Screen.ExternalImportSelector.route) {
                ExternalImportSelectorRoute(
                    openImport = { importType ->
                        navController.navigate(Screen.ExternalImport.routeWithArgs(NavArg.ImportType to importType.name))
                    }
                )
            }

            composable(Screen.ExternalImport.route, listOf(NavArg.ImportType)) {
                val importType = enumValueOf<ImportType>(it.arguments!!.getString(NavArg.ImportType.name)!!)

                ExternalImportRoute(
                    openScanner = {
                        navController.navigate(Screen.ExternalImportScan.routeWithArgs(NavArg.ImportType to importType.name))
                    },
                    openResult = { encodedFileUri ->
                        navController.navigate(
                            Screen.ExternalImportResult.routeWithArgs(
                                NavArg.ImportType to importType.name,
                                NavArg.ImportFileUri to encodedFileUri,
                            )
                        )
                    }
                )
            }

            composable(Screen.ExternalImportScan.route, listOf(NavArg.ImportType)) {
                val importType = enumValueOf<ImportType>(it.arguments!!.getString(NavArg.ImportType.name)!!)

                ExternalImportScanRoute(
                    openResult = { encodedFileContent ->
                        navController.navigate(
                            Screen.ExternalImportResult.routeWithArgs(
                                NavArg.ImportType to importType.name,
                                NavArg.ImportFileContent to encodedFileContent,
                            )
                        )
                    }
                )
            }

            composable(Screen.ExternalImportResult.route, listOf(NavArg.ImportType, NavArg.ImportFileUri)) {
                ExternalImportResultRoute(
                    openSettings = { navController.popBackStack(Screen.ExternalImportSelector.route, true) },
                    openImport = { navController.popBackStack(Screen.ExternalImport.route, false) }
                )
            }
        }
    }
}