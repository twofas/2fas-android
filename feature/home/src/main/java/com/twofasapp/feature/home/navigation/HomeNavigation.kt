package com.twofasapp.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.twofasapp.common.navigation.NavGraph
import com.twofasapp.feature.home.ui.HomeRoute

object HomeGraph : NavGraph {
    override val route: String = "home"
}

fun NavGraphBuilder.homeNavigation(
) {
    composable(HomeGraph.route) {
        HomeRoute(
        )
    }
}