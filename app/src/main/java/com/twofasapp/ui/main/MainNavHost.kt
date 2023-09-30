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
import com.twofasapp.common.ktx.encodeBase64ToString
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.designsystem.common.ModalBottomSheet
import com.twofasapp.feature.about.navigation.AboutLicensesRoute
import com.twofasapp.feature.about.navigation.AboutRoute
import com.twofasapp.feature.appsettings.navigation.AppSettingsRoute
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
import com.twofasapp.feature.home.navigation.HomeNavigationListener
import com.twofasapp.feature.home.navigation.guidesNavigation
import com.twofasapp.feature.home.navigation.homeNavigation
import com.twofasapp.feature.home.ui.services.add.AddServiceModal
import com.twofasapp.feature.home.ui.services.focus.FocusServiceModal
import com.twofasapp.feature.home.ui.services.focus.FocusServiceModalNavArg
import com.twofasapp.feature.security.navigation.securityNavigation
import com.twofasapp.feature.security.ui.lock.LockActivity
import com.twofasapp.feature.startup.navigation.StartupRoute
import com.twofasapp.feature.trash.navigation.DisposeRoute
import com.twofasapp.feature.trash.navigation.TrashRoute
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

            composable(Screen.Startup.route) {
                StartupRoute(openHome = { navController.navigate(Screen.Services.route) { popUpTo(0) } })
            }

            homeNavigation(
                navController = navController,
                listener = object : HomeNavigationListener {
                    override fun openService(activity: Activity, serviceId: Long) {
                        navController.navigate(Screen.EditService.routeWithArgs(NavArg.ServiceId to serviceId))
                    }

                    override fun openExternalImport() {
                        navController.navigate(Screen.ExternalImportSelector.route)
                    }

                    override fun openBrowserExt() {
                        navController.navigate(Screen.BrowserExt.route)
                    }

                    override fun openSecurity(activity: Activity) {
                        navController.navigate(Screen.Security.route)
                    }

                    override fun openBackup(turnOnBackup: Boolean) {
                        navController.navigate(Screen.Backup.routeWithArgs(NavArg.TurnOnBackup to turnOnBackup))
                    }

                    override fun openAppSettings() {
                        navController.navigate(Screen.AppSettings.route)
                    }

                    override fun openTrash() {
                        navController.navigate(Screen.Trash.route)
                    }

                    override fun openNotifications() {
                        navController.navigate(Screen.Notifications.route)
                    }

                    override fun openAbout() {
                        navController.navigate(Screen.About.route)
                    }

                    override fun openAddServiceModal() {
                        recentlyAddedService = null
                        navController.navigate(Modal.AddService.route)
                    }

                    override fun openFocusServiceModal(id: Long) {
                        navController.navigate(Modal.FocusService.route.replace("{id}", id.toString()))
                    }

                    override fun openBackupImport(filePath: String?) {
                        navController.navigate(Screen.BackupImport.routeWithArgs(NavArg.ImportFileUri to filePath?.encodeBase64ToString()))
                    }
                },
                openEditServiceAuth = { onSuccess ->
                    authSuccessCallback = onSuccess

                    startAuthForResult.launch(context.intentFor<LockActivity>("canGoBack" to true))
                }
            )

            securityNavigation(navController = navController)
            guidesNavigation(navController = navController)

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
                        navController.navigate(Screen.EditService.routeWithArgs(NavArg.ServiceId to it))
                        scope.launch { bottomSheetState.hide() }
                    }
                )
            }

            composable(Screen.AppSettings.route) {
                AppSettingsRoute()
            }

            composable(Screen.About.route) {
                AboutRoute(openLicenses = { navController.navigate(Screen.AboutLicenses.route) })
            }

            composable(Screen.AboutLicenses.route) {
                AboutLicensesRoute()
            }

            composable(Screen.Trash.route) {
                TrashRoute(
                    openDispose = { navController.navigate(Screen.Dispose.routeWithArgs(NavArg.ServiceId to it)) }
                )
            }

            composable(Screen.Dispose.route, listOf(NavArg.ServiceId)) {
                DisposeRoute(
                    navigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Backup.route, listOf(NavArg.TurnOnBackup)) {
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

            composable(Screen.BackupImport.route, listOf(NavArg.ImportFileUri)) {
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