package com.twofasapp.feature.browserext.notification

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.twofasapp.browserextension.ui.browser.BrowserDetailsScreen
import com.twofasapp.browserextension.ui.main.BrowserExtensionScreen
import com.twofasapp.browserextension.ui.main.permission.BrowserExtensionPermissionScreen
import com.twofasapp.browserextension.ui.pairing.progress.PairingProgressScreen
import com.twofasapp.browserextension.ui.pairing.scan.PairingScanScreen
import com.twofasapp.android.navigation.NavGraph
import com.twofasapp.android.navigation.NavNode
import com.twofasapp.android.navigation.withArg
import com.twofasapp.feature.browserext.notification.NavArg.ExtensionId

object BrowserExtGraph : com.twofasapp.android.navigation.NavGraph {
    override val route: String = "browserext"
}

internal object NavArg {
    val ExtensionId = navArgument("id") { type = NavType.StringType }
}

private sealed class Node(override val path: String) : com.twofasapp.android.navigation.NavNode {
    override val graph: com.twofasapp.android.navigation.NavGraph = BrowserExtGraph

    object Main : Node("main")
    object Permission : Node("permission")
    object PairingScan : Node("pairing/scan")
    object PairingProgress : Node("pairing/progress?extensionId={${ExtensionId.name}}")
    object BrowserDetails : Node("details/{${ExtensionId.name}}")

}

fun NavGraphBuilder.browserExtNavigation(
    navController: NavHostController,
) {
    navigation(
        route = BrowserExtGraph.route,
        startDestination = Node.Main.route
    ) {

        composable(
            route = Node.Main.route,
        ) {
            BrowserExtensionScreen(
                openPairingScan = { navController.navigate(Node.PairingScan.route) },
                openBrowserDetails = { navController.navigate(Node.BrowserDetails.route.withArg(ExtensionId, it)) },
            )
        }

        composable(Node.PairingScan.route) {
            PairingScanScreen(
                openPairingProgress = {
                    navController.navigate(Node.PairingProgress.route.withArg(ExtensionId, it)) {
                        popUpTo(Node.Main.route)
                    }
                }
            )
        }

        composable(
            route = Node.PairingProgress.route,
            arguments = listOf(ExtensionId)
        ) {
            PairingProgressScreen(
                openMain = { navController.popBackStack(Node.Main.route, false) },
                openPairingScan = {
                    navController.navigate(Node.PairingScan.route) {
                        popUpTo(Node.Main.route)
                    }
                },
                openPermission = {
                    navController.navigate(Node.Permission.route) {
                        popUpTo(Node.Main.route)
                    }
                },
                extensionId = navController.currentBackStackEntry!!.arguments!!.getString(ExtensionId.name, "")
            )
        }

        composable(
            route = Node.BrowserDetails.route,
            arguments = listOf(ExtensionId)
        ) {
            BrowserDetailsScreen(
                onFinish = { navController.popBackStack(Node.Main.route, false) },
                extensionId = navController.currentBackStackEntry!!.arguments!!.getString(ExtensionId.name, "")
            )
        }

        composable(
            route = Node.Permission.route,
        ) {
            BrowserExtensionPermissionScreen()
        }
    }
}