package com.twofasapp.feature.home.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.twofasapp.android.navigation.LegacyScreen
import com.twofasapp.android.navigation.NavArg
import com.twofasapp.feature.home.ui.editservice.EditServiceScreenRoute
import com.twofasapp.feature.home.ui.notifications.NotificationsScreen
import com.twofasapp.feature.home.ui.services.ServicesRoute

fun NavGraphBuilder.homeNavigation(
    navController: NavController,
    listener: HomeNavigationListener,
    openEditServiceAuth: (successCallback: () -> Unit) -> Unit,
) {
    composable(LegacyScreen.Services.route) {
        ServicesRoute(
            listener = listener,
        )
    }

    composable(LegacyScreen.Notifications.route) {
        NotificationsScreen()
    }

    composable(LegacyScreen.EditService.route, listOf(NavArg.ServiceId)) {
        EditServiceScreenRoute(
            serviceId = it.arguments?.getLong(NavArg.ServiceId.name) ?: 0L,
            openAuth = openEditServiceAuth,
        )
    }
}

@Composable
fun NotificationsRoute() {
    NotificationsScreen()
}

@Composable
fun EditServiceRoute(
    serviceId: Long,
    openAuth: (successCallback: () -> Unit) -> Unit,
) {
    EditServiceScreenRoute(
        serviceId = serviceId,
        openAuth = openAuth,
    )
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