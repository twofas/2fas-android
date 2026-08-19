package com.twofasapp.ui.main

import android.app.Activity
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.twofasapp.android.navigation.Screen
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.feature.home.navigation.HomeNavigationListener
import com.twofasapp.feature.home.navigation.ServicesEntry
import com.twofasapp.feature.home.navigation.SettingsEntry
import com.twofasapp.feature.home.ui.bottombar.BottomBarListener
import com.twofasapp.feature.startup.navigation.StartupRoute
import org.koin.compose.koinInject
import timber.log.Timber

@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
@Composable
internal fun MainNavDisplay(
    startDestination: Screen,
    onServiceAddedSuccessfully: (RecentlyAddedService) -> Unit,
    navigator: AppNavigator = koinInject(),
) {
    val backStack = remember(startDestination) {
        navigator.setStartRoot(startDestination)
        navigator.backStack
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

            override fun openService(activity: Activity, serviceId: Long) = todo("EditService")
            override fun openExternalImport() = todo("ExternalImport")
            override fun openBrowserExt() = todo("BrowserExt")
            override fun openSecurity(activity: Activity) = todo("Security")
            override fun openBackup(turnOnBackup: Boolean) = todo("Backup")
            override fun openAppSettings() = todo("AppSettings")
            override fun openTrash() = todo("Trash")
            override fun openNotifications() = todo("Notifications")
            override fun openAbout() = todo("About")
            override fun openAddServiceModal() = todo("AddServiceModal")
            override fun openFocusServiceModal(id: Long) = todo("FocusServiceModal")
            override fun openBackupImport(filePath: String?) = todo("BackupImport")
        }
    }

    val bottomBarListener = remember {
        object : BottomBarListener {
            override fun openHome() = selectTab(Screen.Services)
            override fun openSettings() = selectTab(Screen.Settings)
        }
    }

    val currentDestination = backStack.lastOrNull()
    val showNavigationBar = when (currentDestination) {
        Screen.Services,
        Screen.Settings,
        -> true

        else -> false
    }

    val layoutType = if (showNavigationBar) {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
    } else {
        NavigationSuiteType.None
    }

    NavigationSuiteScaffold(
        layoutType = layoutType,
        navigationSuiteItems = {
            mainNavigationSuiteItems(
                currentDestination = currentDestination,
                onTabSelected = { selectTab(it) },
            )
        },
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { navigator.back() },
            entryProvider = entryProvider {
                entry<Screen.Startup> {
                    StartupRoute()
                }

                entry<Screen.Services> {
                    ServicesEntry(
                        listener = listener,
                        bottomBarListener = bottomBarListener,
                        showBottomBar = false,
                    )
                }

                entry<Screen.Settings> {
                    SettingsEntry(
                        listener = listener,
                        bottomBarListener = bottomBarListener,
                        showBottomBar = false,
                    )
                }
            },
        )
    }
}