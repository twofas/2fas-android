package com.twofasapp.ui.main

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.twofasapp.android.navigation.Screen
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.feature.appsettings.navigation.CustomizationRoute
import com.twofasapp.feature.developer.navigation.DeveloperRoute
import com.twofasapp.feature.home.navigation.HomeNavigationListener
import com.twofasapp.feature.home.ui.services.ServicesRoutePublic
import com.twofasapp.feature.home.ui.settings.SettingsRoute
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
            override fun openDeveloper() = navigator.open(Screen.Developer)
            override fun openAddServiceModal() = todo("AddServiceModal")
            override fun openFocusServiceModal(id: Long) = todo("FocusServiceModal")
            override fun openBackupImport(filePath: String?) = todo("BackupImport")
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
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = MdtTheme.color.background,
        ),
        navigationSuiteItems = {
            mainNavigationSuiteItems(
                currentDestination = currentDestination,
                onTabSelected = { selectTab(it) },
            )
        },
    ) {
        Column {
            NavDisplay(
                modifier = Modifier.weight(1f),
                backStack = backStack,
                onBack = { navigator.back() },
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
                },
            )

            if (showNavigationBar) {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MdtTheme.color.surfaceContainer,
                )
            }
        }
    }
}