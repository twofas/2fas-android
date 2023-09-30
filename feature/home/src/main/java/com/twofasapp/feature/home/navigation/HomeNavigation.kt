package com.twofasapp.feature.home.navigation

import android.app.Activity
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.twofasapp.android.navigation.NavArg
import com.twofasapp.android.navigation.Screen
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
                route = Screen.Services.route,
                inclusive = false,
                saveState = true
            )
        }

        override fun openSettings() {
            navController.navigate(Screen.Settings.route) {
                popUpTo(Screen.Services.route) { inclusive = false }
            }
        }
    }

    composable(Screen.Services.route) {
        ServicesRoute(
            listener = listener,
            bottomBarListener = bottomBarListener,
        )
    }

    composable(Screen.Settings.route) {
        SettingsRoute(listener, bottomBarListener)
    }

    composable(Screen.Notifications.route) {
        NotificationsScreen()
    }

    composable(Screen.EditService.route, listOf(NavArg.ServiceId)) {
        EditServiceScreenRoute(
            navController = navController,
            openSecurity = { navController.navigate(Screen.Security.route) },
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
    fun openAddServiceModal()
    fun openFocusServiceModal(id: Long)
    fun openBackupImport(filePath: String?)
}