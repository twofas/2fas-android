package com.twofasapp.ui.main

import android.app.Activity
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.twofasapp.android.navigation.Screen
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.feature.about.navigation.AboutLicensesRoute
import com.twofasapp.feature.about.navigation.AboutRoute
import com.twofasapp.feature.appsettings.navigation.CustomizationRoute
import com.twofasapp.feature.developer.navigation.DeveloperRoute
import com.twofasapp.feature.home.navigation.HomeNavigationListener
import com.twofasapp.feature.home.ui.services.ServicesRoutePublic
import com.twofasapp.feature.home.ui.settings.SettingsRoute
import com.twofasapp.feature.startup.navigation.StartupRoute
import com.twofasapp.feature.trash.navigation.TrashRoute
import org.koin.compose.koinInject
import timber.log.Timber

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

                entry<Screen.Trash> {
                    TrashRoute()
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