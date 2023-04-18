package com.twofasapp.security.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.twofasapp.common.navigation.NavGraph
import com.twofasapp.common.navigation.NavNode
import com.twofasapp.security.ui.changepin.ChangePinScreen
import com.twofasapp.security.ui.disablepin.DisablePinScreen
import com.twofasapp.security.ui.security.SecurityScreen
import com.twofasapp.security.ui.setuppin.SetupPinScreen

object SecurityGraph : NavGraph {
    override val route: String = "security"
}

private sealed class Node(override val path: String) : NavNode {
    override val graph: NavGraph = SecurityGraph

    object Main : Node("main")
    object SetupPin : Node("pin/setup")
    object DisablePin : Node("pin/disable")
    object ChangePin : Node("pin/change")
}

fun NavGraphBuilder.securityNavigation(
    navController: NavHostController,
) {
    navigation(
        route = SecurityGraph.route,
        startDestination = Node.Main.route
    ) {

        composable(
            route = Node.Main.route,
        ) {
            SecurityScreen(
                openSetupPin = { navController.navigate(Node.SetupPin.route) { popUpTo(Node.Main.route) } },
                openDisablePin = { navController.navigate(Node.DisablePin.route) },
                openChangePin = { navController.navigate(Node.ChangePin.route) },
            )
        }

        composable(
            route = Node.SetupPin.route,
        ) {
            SetupPinScreen()
        }

        composable(
            route = Node.DisablePin.route,
        ) {
            DisablePinScreen()
        }

        composable(
            route = Node.ChangePin.route,
        ) {
            ChangePinScreen(
                openSetupPin = { navController.navigate(Node.SetupPin.route) { popUpTo(Node.Main.route) } },
            )
        }
    }
}