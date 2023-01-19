package com.twofasapp.feature.startup.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.twofasapp.common.navigation.NavGraph
import com.twofasapp.feature.startup.ui.StartupRoute

object StartupGraph : NavGraph {
    override val route: String = "startup"
}

fun NavGraphBuilder.startupNavigation(
    onFinish: () -> Unit
) {
    composable(route = StartupGraph.route) {
        StartupRoute(onFinish)
    }
}
