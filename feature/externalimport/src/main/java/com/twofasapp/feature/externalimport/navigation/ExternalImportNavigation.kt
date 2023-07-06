package com.twofasapp.feature.externalimport.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.twofasapp.android.navigation.NavGraph
import com.twofasapp.android.navigation.NavNode
import com.twofasapp.android.navigation.withArg
import com.twofasapp.feature.externalimport.navigation.ExternalImportNode.Aegis
import com.twofasapp.feature.externalimport.navigation.ExternalImportNode.AuthenticatorPro
import com.twofasapp.feature.externalimport.navigation.ExternalImportNode.GoogleAuthenticator
import com.twofasapp.feature.externalimport.navigation.ExternalImportNode.LastPass
import com.twofasapp.feature.externalimport.navigation.ExternalImportNode.Raivo
import com.twofasapp.feature.externalimport.navigation.ExternalImportNode.Result
import com.twofasapp.feature.externalimport.navigation.ExternalImportNode.Scan
import com.twofasapp.feature.externalimport.navigation.ExternalImportNode.Selector
import com.twofasapp.feature.externalimport.ui.aegis.AegisRoute
import com.twofasapp.feature.externalimport.ui.authenticatorpro.AuthenticatorProRoute
import com.twofasapp.feature.externalimport.ui.googleauthenticator.GoogleAuthenticatorRoute
import com.twofasapp.feature.externalimport.ui.lastpass.LastPassRoute
import com.twofasapp.feature.externalimport.ui.raivo.RaivoRoute
import com.twofasapp.feature.externalimport.ui.result.ImportResultRoute
import com.twofasapp.feature.externalimport.ui.scan.ImportScanRoute
import com.twofasapp.feature.externalimport.ui.selector.SelectorRoute

object ExternalImportGraph : NavGraph {
    override val route: String = "externalimport"
}

enum class ImportType {
    GoogleAuthenticator,
    Aegis,
    Raivo,
    LastPass,
    AuthenticatorPro,
    ;
}

private object NavArg {
    val ImportType = navArgument("importType") { type = NavType.StringType; }
    val ImportContent = navArgument("importContent") { type = NavType.StringType; }
    val StartFromGallery = navArgument("startFromGallery") { type = NavType.BoolType; }
}

private sealed class ExternalImportNode(override val path: String) : NavNode {
    override val graph: NavGraph = ExternalImportGraph

    object Selector : ExternalImportNode("selector")
    object GoogleAuthenticator : ExternalImportNode("googleauthenticator")
    object Aegis : ExternalImportNode("aegis")
    object Raivo : ExternalImportNode("raivo")
    object LastPass : ExternalImportNode("lastpass")
    object AuthenticatorPro : ExternalImportNode("authenticatorpro")
    object Scan : ExternalImportNode("scan?startFromGallery={${NavArg.StartFromGallery.name}}")
    object Result : ExternalImportNode("result/{${NavArg.ImportType.name}}/{${NavArg.ImportContent.name}}")
}

fun NavGraphBuilder.externalImportNavigation(
    navController: NavHostController,
    onFinish: () -> Unit,
) {
    navigation(
        route = ExternalImportGraph.route,
        startDestination = Selector.route
    ) {

        composable(route = Selector.route) {
            SelectorRoute(
                onGoogleAuthenticatorClick = { navController.navigate(GoogleAuthenticator.route) },
                onAegisClick = { navController.navigate(Aegis.route) },
                onRaivoClick = { navController.navigate(Raivo.route) },
                onLastPassClick = { navController.navigate(LastPass.route) },
                onAuthenticatorProClick = { navController.navigate(AuthenticatorPro.route) },
            )
        }

        composable(route = GoogleAuthenticator.route) {
            GoogleAuthenticatorRoute(
                onScanClick = {
                    navController.navigate(
                        Scan.route.withArg(NavArg.StartFromGallery, it)
                    )
                }
            )
        }

        composable(route = Aegis.route) {
            AegisRoute(onFilePicked = { content ->
                navController.navigate(
                    Result.route
                        .withArg(NavArg.ImportType, ImportType.Aegis.name)
                        .withArg(NavArg.ImportContent, content)
                )
            })
        }

        composable(route = Raivo.route) {
            RaivoRoute(onFilePicked = { content ->
                navController.navigate(
                    Result.route
                        .withArg(NavArg.ImportType, ImportType.Raivo.name)
                        .withArg(NavArg.ImportContent, content)
                )
            })
        }

        composable(route = LastPass.route) {
            LastPassRoute(onFilePicked = { content ->
                navController.navigate(
                    Result.route
                        .withArg(NavArg.ImportType, ImportType.LastPass.name)
                        .withArg(NavArg.ImportContent, content)
                )
            })
        }

        composable(route = AuthenticatorPro.route) {
            AuthenticatorProRoute(onFilePicked = { content ->
                navController.navigate(
                    Result.route
                        .withArg(NavArg.ImportType, ImportType.AuthenticatorPro.name)
                        .withArg(NavArg.ImportContent, content)
                )
            })
        }

        composable(
            route = Scan.route,
            arguments = listOf(NavArg.StartFromGallery)
        ) {
            ImportScanRoute(
                startFromGallery = it.arguments?.getBoolean(NavArg.StartFromGallery.name) ?: false,
                onScanned = { content ->
                    navController.navigate(
                        Result.route
                            .withArg(NavArg.ImportType, ImportType.GoogleAuthenticator.name)
                            .withArg(NavArg.ImportContent, content)
                    )
                }
            )
        }

        composable(
            route = Result.route,
            arguments = listOf(NavArg.ImportType, NavArg.ImportContent)
        ) {
            ImportResultRoute(
                type = it.arguments?.getString(NavArg.ImportType.name).orEmpty(),
                content = it.arguments?.getString(NavArg.ImportContent.name).orEmpty(),
                onFinish = onFinish
            )
        }
    }
}
