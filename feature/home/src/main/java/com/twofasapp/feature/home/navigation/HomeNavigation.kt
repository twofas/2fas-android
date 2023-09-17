package com.twofasapp.feature.home.navigation

import android.app.Activity
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.twofasapp.feature.home.ui.bottombar.BottomBarListener
import com.twofasapp.feature.home.ui.services.ServicesRoute
import com.twofasapp.feature.home.ui.settings.SettingsRoute

object HomeGraph : com.twofasapp.android.navigation.NavGraph {
    override val route: String = "home"
}

sealed class HomeNode(override val path: String) : com.twofasapp.android.navigation.NavNode {
    override val graph: com.twofasapp.android.navigation.NavGraph = HomeGraph

    object Services : HomeNode("services")
    object Settings : HomeNode("settings")
}

fun NavGraphBuilder.homeNavigation(
    navController: NavController,
    listener: HomeNavigationListener,
) {
    val bottomBarListener = object : BottomBarListener {
        override fun openHome() {
            navController.navigate(HomeNode.Services.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        override fun openSettings() {
            navController.navigate(HomeNode.Settings.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }

        }
    }

    navigation(
        route = HomeGraph.route,
        startDestination = HomeNode.Services.route
    ) {
        composable(HomeNode.Services.route) {
            ServicesRoute(
                listener = listener,
                bottomBarListener = bottomBarListener,
            )
        }

        composable(HomeNode.Settings.route) {
            SettingsRoute(listener, bottomBarListener)
        }
    }
}

interface HomeNavigationListener {
    fun openService(activity: Activity, serviceId: Long)
    fun openExternalImport()
    fun openBrowserExt()
    fun openSecurity(activity: Activity)
    fun openBackup()
    fun openAppSettings()
    fun openTrash()
    fun openNotifications()
    fun openAbout()
    fun openAddServiceModal()
    fun openFocusServiceModal(id: Long)
}