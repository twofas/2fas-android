package com.twofasapp.feature.home.navigation

import android.app.Activity
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.twofasapp.android.navigation.LegacyScreen
import com.twofasapp.android.navigation.NavArg
import com.twofasapp.feature.home.ui.bottombar.BottomBarListener
import com.twofasapp.feature.home.ui.editservice.EditServiceScreenRoute
import com.twofasapp.feature.home.ui.notifications.NotificationsScreen
import com.twofasapp.feature.home.ui.services.ServicesRoute
import com.twofasapp.feature.home.ui.settings.SettingsRoute

fun NavGraphBuilder.homeNavigation(
    navController: NavController,
    listener: HomeNavigationListener,
    openEditServiceAuth: (successCallback: () -> Unit) -> Unit,
) {
    val bottomBarListener = object : BottomBarListener {
        override fun openHome() {
            navController.popBackStack(
                route = LegacyScreen.Services.route,
                inclusive = false,
                saveState = true,
            )
        }

        override fun openSettings() {
            navController.navigate(LegacyScreen.Settings.route) {
                popUpTo(LegacyScreen.Services.route) { inclusive = false }
            }
        }
    }

    composable(LegacyScreen.Services.route) {
        ServicesRoute(
            listener = listener,
            bottomBarListener = bottomBarListener,
        )
    }

    composable(LegacyScreen.Settings.route) {
        SettingsRoute()
    }

    composable(LegacyScreen.Notifications.route) {
        NotificationsScreen(
            openInternalRoute = { route ->
                when (route) {
                    LegacyScreen.Backup.route -> {
                        navController.navigate(LegacyScreen.Backup.routeWithArgs(NavArg.TurnOnBackup to true))
                    }

                    else -> {
                        navController.navigate(route)
                    }
                }
            },
        )
    }

    composable(LegacyScreen.EditService.route, listOf(NavArg.ServiceId)) {
        EditServiceScreenRoute(
            navController = navController,
            openSecurity = { navController.navigate(LegacyScreen.Security.route) },
            openAuth = openEditServiceAuth,
        )
    }
}

interface HomeNavigationListener {
    fun openService(activity: Activity, serviceId: Long)
    fun openExternalImport()
    fun openBrowserExt()
    fun openSecurity(activity: Activity)
    fun openBackup(turnOnBackup: Boolean)
    fun openAppSettings()
    fun openTrash()
    fun openNotifications()
    fun openAbout()
    fun openDeveloper()
    fun openAddServiceModal()
    fun openFocusServiceModal(id: Long)
    fun openBackupImport(filePath: String?)
}