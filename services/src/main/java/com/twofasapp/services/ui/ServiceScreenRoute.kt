package com.twofasapp.services.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.twofasapp.common.navigation.NavGraph
import com.twofasapp.common.navigation.NavNode
import com.twofasapp.services.ui.advancedsettings.AdvancedSettingsScreen
import com.twofasapp.services.ui.changebrand.ChangeBrandScreen
import com.twofasapp.services.ui.changelabel.ChangeLabelScreen
import com.twofasapp.services.ui.deleteservice.DeleteServiceScreen
import com.twofasapp.services.ui.domainassignment.DomainAssignmentScreen
import com.twofasapp.services.ui.requesticon.RequestIconScreen
import org.koin.androidx.compose.koinViewModel

internal object ServiceInternalGraph : NavGraph {
    override val route: String = "service_internal"
}


private sealed class Node(override val path: String) : NavNode {
    override val graph: NavGraph = ServiceInternalGraph

    object Main : Node("main")
    object DomainAssignment : Node("domain_assignment")
    object AdvancedSettings : Node("advanced_settings")
    object ChangeBrand : Node("change_brand")
    object ChangeLabel : Node("change_label")
    object RequestIcon : Node("request_icon")
    object Delete : Node("delete_service")
}

@Composable
internal fun ServiceScreenRoute(
    navController: NavController,
    serviceViewModel: ServiceViewModel = koinViewModel(),
) {
    val navHostController = rememberNavController()

    NavHost(navHostController, startDestination = ServiceInternalGraph.route) {
        navigation(route = ServiceInternalGraph.route, startDestination = Node.Main.route) {

            composable(Node.Main.route) {

                ServiceScreen(
                    onBackClick = { navController.popBackStack() },
                    onAdvanceClick = { navHostController.navigate(Node.AdvancedSettings.route) },
                    onChangeBrandClick = { navHostController.navigate(Node.ChangeBrand.route) },
                    onChangeLabelClick = { navHostController.navigate(Node.ChangeLabel.route) },
                    onDomainAssignmentClick = { navHostController.navigate(Node.DomainAssignment.route) },
                    onDeleteClick = { navHostController.navigate(Node.Delete.route) },
                    onSecurityClick = {
//                                externalNavigator.openSecurity(this@ServiceActivity)
                    },
                    onAuthenticateSecretClick = {
//                                externalNavigator.openAuthenticate(this@ServiceActivity)
                    },
                    viewModel = serviceViewModel,
                )
            }

            composable(Node.DomainAssignment.route) {
                DomainAssignmentScreen(
                    viewModel = serviceViewModel,
                )
            }
        }

        composable(Node.AdvancedSettings.route) {
            AdvancedSettingsScreen(
                viewModel = serviceViewModel,
            )
        }

        composable(Node.ChangeBrand.route) {
            ChangeBrandScreen(
                onRequestIconClick = { navHostController.navigate(Node.RequestIcon.route) },
                viewModel = serviceViewModel,
            )
        }

        composable(Node.ChangeLabel.route) {
            ChangeLabelScreen(
                viewModel = serviceViewModel,
            )
        }

        composable(Node.RequestIcon.route) {
            RequestIconScreen()
        }

        composable(Node.Delete.route) {
            DeleteServiceScreen(
                viewModel = serviceViewModel,
            )
        }
    }
}