package com.twofasapp.ui.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.twofasapp.feature.home.navigation.HomeGraph
import com.twofasapp.feature.home.navigation.homeNavigation
import com.twofasapp.feature.startup.navigation.startupNavigation


@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(navController = navController, startDestination = startDestination) {
        startupNavigation(
            onFinish = { navController.navigate(HomeGraph.route) { popUpTo(0) } }
        )

        homeNavigation()
    }
}