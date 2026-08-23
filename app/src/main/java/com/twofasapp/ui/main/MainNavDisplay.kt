package com.twofasapp.ui.main

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.twofasapp.android.navigation.Screen
import com.twofasapp.android.navigation.intentFor
import com.twofasapp.common.ktx.legacyEncodeBase64ToString
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.feature.about.navigation.AboutLicensesRoute
import com.twofasapp.feature.about.navigation.AboutRoute
import com.twofasapp.feature.appsettings.navigation.CustomizationRoute
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
import com.twofasapp.feature.home.navigation.EditServiceRoute
import com.twofasapp.feature.home.navigation.GuideInitRoute
import com.twofasapp.feature.home.navigation.GuidePagerRoute
import com.twofasapp.feature.home.navigation.GuidesRoute
import com.twofasapp.feature.home.navigation.HomeNavigationListener
import com.twofasapp.feature.home.navigation.NotificationsRoute
import com.twofasapp.feature.home.ui.services.ServicesRoutePublic
import com.twofasapp.feature.home.ui.settings.SettingsRoute
import com.twofasapp.feature.security.navigation.ChangePinRoute
import com.twofasapp.feature.security.navigation.DisablePinRoute
import com.twofasapp.feature.security.navigation.SecurityRoute
import com.twofasapp.feature.security.navigation.SetupPinRoute
import com.twofasapp.feature.security.ui.lock.LockActivity
import com.twofasapp.feature.startup.navigation.StartupRoute
import com.twofasapp.feature.trash.navigation.DisposeRoute
import com.twofasapp.feature.trash.navigation.TrashRoute
import org.koin.compose.koinInject
import timber.log.Timber

@Composable
internal fun MainNavDisplay(
    startDestination: Screen,
    onServiceAddedSuccessfully: (RecentlyAddedService) -> Unit,
    navigator: AppNavigator = koinInject(),
) {
    val context = LocalContext.current
    val backStack = remember(startDestination) {
        navigator.setStartRoot(startDestination)
        navigator.backStack
    }

    val authSuccessCallback = remember { mutableStateOf<() -> Unit>({}) }
    val startAuthForResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                authSuccessCallback.value()
            }
        }

    fun openAuth(onSuccess: () -> Unit) {
        authSuccessCallback.value = onSuccess
        startAuthForResult.launch(context.intentFor<LockActivity>("canGoBack" to true))
    }

    fun selectTab(tab: Screen) {
        while (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
        if (backStack.firstOrNull() != tab) {
            backStack.add(tab)
        }
    }

    val listener = remember {
        object : HomeNavigationListener {
            private fun todo(destination: String) {
                Timber.tag("Nav3").w("Destination '$destination' not migrated yet - use useNavigation3 = false")
            }

            override fun openService(activity: Activity, serviceId: Long) = navigator.open(Screen.EditService(serviceId = serviceId))
            override fun openExternalImport() = navigator.open(Screen.ExternalImportSelector)
            override fun openBrowserExt() = navigator.open(Screen.BrowserExt)
            override fun openSecurity(activity: Activity) = navigator.open(Screen.Security)
            override fun openBackup(turnOnBackup: Boolean) = navigator.open(Screen.Backup)
            override fun openAppSettings() = todo("AppSettings")
            override fun openTrash() = navigator.open(Screen.Trash)
            override fun openNotifications() = navigator.open(Screen.Notifications)
            override fun openAbout() = navigator.open(Screen.About)
            override fun openDeveloper() = navigator.open(Screen.Developer)
            override fun openAddServiceModal() = todo("AddServiceModal")
            override fun openFocusServiceModal(id: Long) = todo("FocusServiceModal")
            override fun openBackupImport(filePath: String?) =
                navigator.open(Screen.BackupImport(importFileUri = filePath?.legacyEncodeBase64ToString()))
        }
    }

    val currentDestination = backStack.lastOrNull()
    val showNavigationBar = when (currentDestination) {
        Screen.Services,
        Screen.Settings,
        -> true

        else -> false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MdtTheme.color.background)
            .then(if (showNavigationBar) Modifier else Modifier.navigationBarsPadding()),
    ) {
        NavDisplay(
            modifier = Modifier.weight(1f),
            backStack = backStack,
            onBack = { navigator.back() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            transitionSpec = { fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250)) },
            popTransitionSpec = { fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250)) },
            predictivePopTransitionSpec = { fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250)) },
            entryProvider = entryProvider {
                entry<Screen.Startup> {
                    StartupRoute()
                }

                entry<Screen.Developer> {
                    DeveloperRoute()
                }

                entry<Screen.Services> {
                    ServicesRoutePublic(
                        listener = listener,
                    )
                }

                entry<Screen.Settings> {
                    SettingsRoute()
                }

                entry<Screen.Customization> {
                    CustomizationRoute()
                }

                entry<Screen.Notifications> {
                    NotificationsRoute()
                }

                entry<Screen.EditService> { key ->
                    EditServiceRoute(
                        serviceId = key.serviceId,
                        openAuth = { onSuccess -> openAuth(onSuccess) },
                    )
                }

                entry<Screen.Dispose> { key ->
                    DisposeRoute(serviceId = key.serviceId)
                }

                entry<Screen.ExternalImportSelector> {
                    ExternalImportSelectorRoute()
                }

                entry<Screen.ExternalImport> { key ->
                    ExternalImportRoute(importType = enumValueOf<ImportType>(key.importType))
                }

                entry<Screen.ExternalImportScan> { key ->
                    ExternalImportScanRoute(importType = enumValueOf<ImportType>(key.importType))
                }

                entry<Screen.ExternalImportResult> { key ->
                    ExternalImportResultRoute(
                        importType = enumValueOf<ImportType>(key.importType),
                        importFileUri = key.importFileUri,
                        importFileContent = key.importFileContent,
                    )
                }

                entry<Screen.Guides> {
                    GuidesRoute()
                }

                entry<Screen.GuideInit> { key ->
                    GuideInitRoute(guide = key.guide)
                }

                entry<Screen.GuidePager> { key ->
                    GuidePagerRoute(
                        guide = key.guide,
                        guideVariantIndex = key.guideVariantIndex,
                    )
                }

                entry<Screen.BrowserExt> {
                    BrowserExtRoute()
                }

                entry<Screen.BrowserExtPermission> {
                    BrowserExtPermissionRoute()
                }

                entry<Screen.BrowserExtScan> {
                    BrowserExtScanRoute()
                }

                entry<Screen.BrowserExtPairing> { key ->
                    BrowserExtPairingRoute(extensionId = key.extensionId)
                }

                entry<Screen.BrowserExtDetails> { key ->
                    BrowserExtDetailsRoute(extensionId = key.extensionId)
                }

                entry<Screen.Security> {
                    SecurityRoute()
                }

                entry<Screen.SetupPin> {
                    SetupPinRoute()
                }

                entry<Screen.DisablePin> {
                    DisablePinRoute()
                }

                entry<Screen.ChangePin> {
                    ChangePinRoute()
                }

                entry<Screen.Trash> {
                    TrashRoute()
                }

                entry<Screen.Backup> {
                    BackupRoute()
                }

                entry<Screen.BackupSettings> {
                    BackupSettingsRoute()
                }

                entry<Screen.BackupExport> {
                    BackupExportRoute()
                }

                entry<Screen.BackupImport> { key ->
                    BackupImportRoute(importFileUri = key.importFileUri)
                }

                entry<Screen.About> {
                    AboutRoute()
                }

                entry<Screen.AboutLicenses> {
                    AboutLicensesRoute()
                }
            },
        )

        AnimatedVisibility(
            visible = showNavigationBar,
            enter = slideInVertically { it } + expandVertically(expandFrom = Alignment.Bottom),
            exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + slideOutVertically { it },
        ) {
            MainNavBar(
                currentDestination = currentDestination,
                onTabSelected = { selectTab(it) },
            )
        }
    }
}