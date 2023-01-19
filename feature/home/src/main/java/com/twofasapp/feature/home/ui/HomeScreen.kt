package com.twofasapp.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.twofasapp.designsystem.composable.TwsNavigationBar
import com.twofasapp.designsystem.composable.TwsNavigationBarItem
import com.twofasapp.feature.home.R
import com.twofasapp.feature.home.navigation.BottomNavItem
import com.twofasapp.feature.home.navigation.NotificationsGraph
import com.twofasapp.feature.home.navigation.ServicesGraph
import com.twofasapp.feature.home.navigation.SettingsGraph
import com.twofasapp.feature.home.navigation.notificationsNavigation
import com.twofasapp.feature.home.navigation.servicesNavigation
import com.twofasapp.feature.home.navigation.settingsNavigation

@Composable
internal fun HomeRoute() {
    HomeScreen()
}

@Composable
private fun HomeScreen() {
    val bottomNavItems = listOf(
        BottomNavItem(
            title = "Tokens",
            icon = painterResource(id = R.drawable.ic_android),
            route = ServicesGraph.route,
        ),
        BottomNavItem(
            title = "Settings",
            icon = painterResource(id = R.drawable.ic_android),
            route = SettingsGraph.route,
        ),
        BottomNavItem(
            title = "Notifications",
            icon = painterResource(id = R.drawable.ic_android),
            route = NotificationsGraph.route,
        ),
    )

    val homeNavController = rememberNavController()
    val navBackStackState = homeNavController.currentBackStackEntryAsState().value

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {

        NavHost(
            navController = homeNavController,
            startDestination = ServicesGraph.route,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
        ) {
            servicesNavigation()
            settingsNavigation()
            notificationsNavigation()
        }

        TwsNavigationBar {
            bottomNavItems.forEach {
                TwsNavigationBarItem(
                    text = it.title,
                    icon = it.icon,
                    selected = navBackStackState?.destination?.route == it.route,
                    onClick = {
                        homeNavController.navigate(it.route) {
                            popUpTo(homeNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}