package com.twofasapp.feature.startup.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.twofasapp.android.navigation.NavGraph
import com.twofasapp.feature.startup.ui.StartupRoute

object StartupGraph : com.twofasapp.android.navigation.NavGraph {
    override val route: String = "startup"
}

fun NavGraphBuilder.startupNavigation(
    openHome: () -> Unit,
) {
    composable(route = StartupGraph.route) {
        StartupRoute(openHome)
    }
}
