package com.twofasapp.feature.about.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.twofasapp.common.navigation.NavGraph
import com.twofasapp.common.navigation.NavNode
import com.twofasapp.feature.about.ui.about.AboutRoute
import com.twofasapp.feature.about.ui.licenses.LicensesRoute

object AboutGraph : NavGraph {
    override val route: String = "about"
}

private sealed class Node(override val path: String) : NavNode {
    override val graph: NavGraph = AboutGraph

    object Main : Node("main")
    object Licenses : Node("licenses")
}

fun NavGraphBuilder.aboutNavigation(
    navController: NavHostController,
) {
    navigation(
        route = AboutGraph.route,
        startDestination = Node.Main.route,
    ) {
        composable(Node.Main.route) {
            AboutRoute(
                openLicenses = { navController.navigate(Node.Licenses.route) }
            )
        }

        composable(Node.Licenses.route) {
            LicensesRoute()
        }
    }
}