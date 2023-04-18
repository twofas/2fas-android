package com.twofasapp.feature.trash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.twofasapp.common.navigation.NavGraph
import com.twofasapp.common.navigation.NavNode
import com.twofasapp.common.navigation.withArg
import com.twofasapp.feature.trash.ui.dispose.DisposeRoute
import com.twofasapp.feature.trash.ui.trash.TrashRoute

object TrashGraph : NavGraph {
    override val route: String = "trash"
}

internal object NavArg {
    val ServiceId = navArgument("id") { type = NavType.LongType }
}

private sealed class Node(override val path: String) : NavNode {
    override val graph: NavGraph = TrashGraph

    object Main : Node("main")
    object Dispose : Node("dispose/{${NavArg.ServiceId.name}}")
}

fun NavGraphBuilder.trashNavigation(
    navController: NavHostController,
) {
    navigation(
        route = TrashGraph.route,
        startDestination = Node.Main.route,
    ) {
        composable(Node.Main.route) {
            TrashRoute(
                openDispose = { navController.navigate(Node.Dispose.route.withArg(NavArg.ServiceId, it)) }
            )
        }

        composable(
            route = Node.Dispose.route,
            arguments = listOf(NavArg.ServiceId)
        ) {
            DisposeRoute(navigateBack = { navController.popBackStack() })
        }
    }
}