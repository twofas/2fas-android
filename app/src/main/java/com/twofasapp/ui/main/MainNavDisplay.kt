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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.twofasapp.android.navigation.BottomBarState
import com.twofasapp.android.navigation.Screen
import com.twofasapp.android.navigation.intentFor
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.feature.about.navigation.AboutLicensesRoute
import com.twofasapp.feature.about.navigation.AboutRoute
import com.twofasapp.feature.appsettings.navigation.CustomizationRoute
import com.twofasapp.feature.backup.navigation.BackupExportRoute
import com.twofasapp.feature.backup.navigation.BackupImportRoute
import com.twofasapp.feature.backup.navigation.BackupRoute
import com.twofasapp.feature.backup.navigation.BackupSettingsRoute
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
import com.twofasapp.feature.guides.navigation.GuideInitRoute
import com.twofasapp.feature.guides.navigation.GuidePagerRoute
import com.twofasapp.feature.guides.navigation.GuidesRoute
import com.twofasapp.feature.home.navigation.EditServiceChangeBrandRoute
import com.twofasapp.feature.home.navigation.EditServiceChangeLabelRoute
import com.twofasapp.feature.home.navigation.EditServiceDomainAssignmentRoute
import com.twofasapp.feature.home.navigation.EditServiceRequestIconRoute
import com.twofasapp.feature.home.navigation.EditServiceRoute
import com.twofasapp.feature.home.navigation.HomeRoute
import com.twofasapp.feature.home.navigation.NotificationsRoute
import com.twofasapp.feature.home.ui.settings.SettingsRoute
import com.twofasapp.feature.security.navigation.ChangePinRoute
import com.twofasapp.feature.security.navigation.DisablePinRoute
import com.twofasapp.feature.security.navigation.SecurityRoute
import com.twofasapp.feature.security.navigation.SetupPinRoute
import com.twofasapp.feature.security.ui.lock.LockActivity
import com.twofasapp.feature.startup.navigation.StartupRoute
import com.twofasapp.feature.trash.navigation.TrashRoute
import org.koin.compose.koinInject

@Composable
internal fun MainNavDisplay(
    startDestination: Screen,
    showBackupError: Boolean,
    onServiceAddedSuccessfully: (RecentlyAddedService) -> Unit,
    navigator: AppNavigator = koinInject(),
    bottomBarState: BottomBarState = koinInject(),
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

    val currentDestination = backStack.lastOrNull()
    val bottomBarVisible by bottomBarState.visible.collectAsStateWithLifecycle()
    val showNavigationBar = when (currentDestination) {
        Screen.Home,
        Screen.Settings,
        -> bottomBarVisible

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

                entry<Screen.Home> {
                    HomeRoute()
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

                entry<Screen.EditServiceDomainAssignment> { key ->
                    EditServiceDomainAssignmentRoute(serviceId = key.serviceId)
                }

                entry<Screen.EditServiceChangeBrand> { key ->
                    EditServiceChangeBrandRoute(serviceId = key.serviceId)
                }

                entry<Screen.EditServiceChangeLabel> { key ->
                    EditServiceChangeLabelRoute(serviceId = key.serviceId)
                }

                entry<Screen.EditServiceRequestIcon> {
                    EditServiceRequestIconRoute()
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
                showBackupError = showBackupError,
                onTabSelected = { selectTab(it) },
            )
        }
    }
}