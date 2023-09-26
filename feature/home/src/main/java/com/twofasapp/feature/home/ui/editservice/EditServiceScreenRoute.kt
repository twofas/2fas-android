package com.twofasapp.feature.home.ui.editservice

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.twofasapp.android.navigation.NavAnimation
import com.twofasapp.feature.home.ui.editservice.advancedsettings.AdvancedSettingsScreen
import com.twofasapp.feature.home.ui.editservice.changebrand.ChangeBrandScreen
import com.twofasapp.feature.home.ui.editservice.changelabel.ChangeLabelScreen
import com.twofasapp.feature.home.ui.editservice.deleteservice.DeleteServiceScreen
import com.twofasapp.feature.home.ui.editservice.domainassignment.DomainAssignmentScreen
import com.twofasapp.feature.home.ui.editservice.requesticon.RequestIconScreen
import org.koin.androidx.compose.koinViewModel

private sealed class EditServiceScreen(val route: String) {
    data object Main : EditServiceScreen("main")
    data object DomainAssignment : EditServiceScreen("domain_assignment")
    data object AdvancedSettings : EditServiceScreen("advanced_settings")
    data object ChangeBrand : EditServiceScreen("change_brand")
    data object ChangeLabel : EditServiceScreen("change_label")
    data object RequestIcon : EditServiceScreen("request_icon")
    data object Delete : EditServiceScreen("delete_service")
}

@Composable
internal fun EditServiceScreenRoute(
    navController: NavController,
    openSecurity: () -> Unit,
    openAuth: (successCallback: () -> Unit) -> Unit,
    serviceViewModel: EditServiceViewModel = koinViewModel(),
) {
    val navHostController = rememberNavController()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navHostController,
            startDestination = "editservice",
            enterTransition = NavAnimation.Enter,
            exitTransition = NavAnimation.Exit,
        ) {
            navigation(route = "editservice", startDestination = EditServiceScreen.Main.route) {

                composable(EditServiceScreen.Main.route) {

                    EditServiceScreen(
                        onBackClick = { navController.popBackStack() },
                        onAdvanceClick = { navHostController.navigate(EditServiceScreen.AdvancedSettings.route) },
                        onChangeBrandClick = { navHostController.navigate(EditServiceScreen.ChangeBrand.route) },
                        onChangeLabelClick = { navHostController.navigate(EditServiceScreen.ChangeLabel.route) },
                        onDomainAssignmentClick = { navHostController.navigate(EditServiceScreen.DomainAssignment.route) },
                        onDeleteClick = { navHostController.navigate(EditServiceScreen.Delete.route) },
                        onSecurityClick = { openSecurity() },
                        onAuthenticateSecretClick = {
                            openAuth {
                                serviceViewModel.secretAuthenticated()
                            }
                        },
                        viewModel = serviceViewModel,
                    )
                }

                composable(EditServiceScreen.DomainAssignment.route) {
                    DomainAssignmentScreen(
                        viewModel = serviceViewModel,
                    )
                }
            }

            composable(EditServiceScreen.AdvancedSettings.route) {
                AdvancedSettingsScreen(
                    viewModel = serviceViewModel,
                )
            }

            composable(EditServiceScreen.ChangeBrand.route) {
                ChangeBrandScreen(
                    onRequestIconClick = { navHostController.navigate(EditServiceScreen.RequestIcon.route) },
                    viewModel = serviceViewModel,
                )
            }

            composable(EditServiceScreen.ChangeLabel.route) {
                ChangeLabelScreen(
                    viewModel = serviceViewModel,
                )
            }

            composable(EditServiceScreen.RequestIcon.route) {
                RequestIconScreen()
            }

            composable(EditServiceScreen.Delete.route) {
                DeleteServiceScreen(
                    viewModel = serviceViewModel,
                )
            }
        }
    }
}