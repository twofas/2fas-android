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
import com.twofasapp.feature.developer.navigation.DeveloperRoute
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
                        navController.navigate(LegacyScreen.Security.route)
                    }

                    override fun openBackup(turnOnBackup: Boolean) {
                        navController.navigate(LegacyScreen.Backup.routeWithArgs(NavArg.TurnOnBackup to turnOnBackup))
                    }

                    override fun openAppSettings() {
                        navController.navigate(LegacyScreen.AppSettings.route)
                    }

                    override fun openTrash() {
                        navController.navigate(LegacyScreen.Trash.route)
                    }

                    override fun openNotifications() {
                        navController.navigate(LegacyScreen.Notifications.route)
                    }

                    override fun openAbout() {
                        navController.navigate(LegacyScreen.About.route)
                    }

                    override fun openDeveloper() {
                        navController.navigate(LegacyScreen.Developer.route)
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

            securityNavigation(navController = navController)
            guidesNavigation(navController = navController)

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

            composable(LegacyScreen.AppSettings.route) {
                AppSettingsRoute()
            }

            composable(LegacyScreen.About.route) {
                AboutRoute(openLicenses = { navController.navigate(LegacyScreen.AboutLicenses.route) })
            }

            composable(LegacyScreen.AboutLicenses.route) {
                AboutLicensesRoute()
            }

            composable(LegacyScreen.Developer.route) {
                DeveloperRoute()
            }

            composable(LegacyScreen.Trash.route) {
                TrashRoute(
                    openDispose = { navController.navigate(LegacyScreen.Dispose.routeWithArgs(NavArg.ServiceId to it)) },
                )
            }

            composable(LegacyScreen.Dispose.route, listOf(NavArg.ServiceId)) {
                DisposeRoute(
                    navigateBack = { navController.popBackStack() },
                )
            }

            composable(LegacyScreen.Backup.route, listOf(NavArg.TurnOnBackup)) {
                BackupRoute(
                    openSettings = { navController.navigate(LegacyScreen.BackupSettings.route) },
                    openExport = { navController.navigate(LegacyScreen.BackupExport.route) },
                    openImport = { navController.navigate(LegacyScreen.BackupImport.routeWithArgs()) },
                    goBack = { navController.popBackStack() },
                )
            }

            composable(LegacyScreen.BackupSettings.route) {
                BackupSettingsRoute(
                    goBack = { navController.popBackStack() },
                )
            }

            composable(LegacyScreen.BackupExport.route) {
                BackupExportRoute(
                    goBack = { navController.popBackStack() },
                )
            }

            composable(LegacyScreen.BackupImport.route, listOf(NavArg.ImportFileUri)) {
                BackupImportRoute(
                    goBack = { navController.popBackStack() },
                )
            }

            composable(LegacyScreen.BrowserExt.route) {
                BrowserExtRoute(
                    openScan = { navController.navigate(LegacyScreen.BrowserExtScan.route) },
                    openDetails = { extensionId ->
                        navController.navigate(LegacyScreen.BrowserExtDetails.routeWithArgs(NavArg.ExtensionId to extensionId))
                    },
                )
            }

            composable(LegacyScreen.BrowserExtPermission.route) {
                BrowserExtPermissionRoute(
                    openMain = { navController.popBackStack(LegacyScreen.BrowserExt.route, false) },
                )
            }

            composable(LegacyScreen.BrowserExtScan.route) {
                BrowserExtScanRoute(
                    openProgress = { extensionId ->
                        navController.navigate(LegacyScreen.BrowserExtPairing.routeWithArgs(NavArg.ExtensionId to extensionId)) {
                            popUpTo(LegacyScreen.BrowserExt.route)
                        }
                    },
                )
            }

            composable(LegacyScreen.BrowserExtPairing.route, listOf(NavArg.ExtensionId)) {
                BrowserExtPairingRoute(
                    openMain = { navController.popBackStack(LegacyScreen.BrowserExt.route, false) },
                    openPermission = { navController.navigate(LegacyScreen.BrowserExtPermission.route) { popUpTo(LegacyScreen.BrowserExt.route) } },
                    openScan = { navController.navigate(LegacyScreen.BrowserExtScan.route) { popUpTo(LegacyScreen.BrowserExt.route) } },
                )
            }

            composable(LegacyScreen.BrowserExtDetails.route, listOf(NavArg.ExtensionId)) {
                BrowserExtDetailsRoute(
                    openMain = { navController.popBackStack(LegacyScreen.BrowserExt.route, false) },
                )
            }

            composable(LegacyScreen.ExternalImportSelector.route) {
                ExternalImportSelectorRoute(
                    openImport = { importType ->
                        navController.navigate(LegacyScreen.ExternalImport.routeWithArgs(NavArg.ImportType to importType.name))
                    },
                )
            }

            composable(LegacyScreen.ExternalImport.route, listOf(NavArg.ImportType)) {
                val importType = enumValueOf<ImportType>(it.arguments!!.getString(NavArg.ImportType.name)!!)

                ExternalImportRoute(
                    openScanner = {
                        navController.navigate(LegacyScreen.ExternalImportScan.routeWithArgs(NavArg.ImportType to importType.name))
                    },
                    openResult = { encodedFileUri ->
                        navController.navigate(
                            LegacyScreen.ExternalImportResult.routeWithArgs(
                                NavArg.ImportType to importType.name,
                                NavArg.ImportFileUri to encodedFileUri,
                            ),
                        )
                    },
                )
            }

            composable(LegacyScreen.ExternalImportScan.route, listOf(NavArg.ImportType)) {
                val importType = enumValueOf<ImportType>(it.arguments!!.getString(NavArg.ImportType.name)!!)

                ExternalImportScanRoute(
                    openResult = { encodedFileContent ->
                        navController.navigate(
                            LegacyScreen.ExternalImportResult.routeWithArgs(
                                NavArg.ImportType to importType.name,
                                NavArg.ImportFileContent to encodedFileContent,
                            ),
                        )
                    },
                )
            }

            composable(LegacyScreen.ExternalImportResult.route, listOf(NavArg.ImportType, NavArg.ImportFileUri)) {
                ExternalImportResultRoute(
                    openSettings = { navController.popBackStack(LegacyScreen.ExternalImportSelector.route, true) },
                    openImport = { navController.popBackStack(LegacyScreen.ExternalImport.route, false) },
                )
            }
        }
    }
}