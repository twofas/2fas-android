package com.twofasapp.services.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.twofasapp.base.BaseComponentActivity
import com.twofasapp.core.log.FileLogger
import com.twofasapp.designsystem.MainAppTheme
import com.twofasapp.navigation.base.withOwner
import com.twofasapp.services.navigation.ServiceGraph
import com.twofasapp.services.navigation.ServiceNode
import com.twofasapp.services.ui.advancedsettings.AdvancedSettingsScreen
import com.twofasapp.services.ui.changebrand.ChangeBrandScreen
import com.twofasapp.services.ui.changelabel.ChangeLabelScreen
import com.twofasapp.services.ui.deleteservice.DeleteServiceScreen
import com.twofasapp.services.ui.domainassignment.DomainAssignmentScreen
import com.twofasapp.services.ui.requesticon.RequestIconScreen
import com.twofasapp.services.ui.service.ServiceScreen
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ServiceActivity : BaseComponentActivity() {

    companion object {
        const val REQUEST_KEY_ADD_SERVICE = 46851
        const val REQUEST_KEY_AUTH_SERVICE = 7895
        const val ARG_SERVICE = "service"
        const val ARG_SERVICE_ID = "serviceId"
        const val ARG_SHOW_ICON_PICKER = "showIconPicker"
        const val RESULT_SERVICE = "service"
    }

    private val viewModel: ServiceViewModel by viewModel()
    private val externalNavigator: ServiceExternalNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelStoreOwner = compositionLocalOf<ViewModelStoreOwner> { this }

        val serviceId = intent.getLongExtra(ARG_SERVICE_ID, 0L)

        FileLogger.logScreen(if (serviceId == 0L) "AddServiceManually" else "EditService")

        viewModel.init(serviceId)

        setContent {
            val navController = rememberNavController()

            MainAppTheme {
                NavHost(navController, startDestination = ServiceGraph.route) {
                    navigation(route = ServiceGraph.route, startDestination = ServiceNode.Main.route) {

                        composable(ServiceNode.Main.route) {
                            withOwner(viewModelStoreOwner.current) {
                                ServiceScreen(
                                    onAdvanceClick = { navController.navigate(ServiceNode.AdvancedSettings.route) },
                                    onChangeBrandClick = { navController.navigate(ServiceNode.ChangeBrand.route) },
                                    onChangeLabelClick = { navController.navigate(ServiceNode.ChangeLabel.route) },
                                    onDomainAssignmentClick = { navController.navigate(ServiceNode.DomainAssignment.route) },
                                    onDeleteClick = { navController.navigate(ServiceNode.Delete.route) },
                                    onSecurityClick = { externalNavigator.openSecurity(this@ServiceActivity) },
                                    onAuthenticateSecretClick = { externalNavigator.openAuthenticate(this@ServiceActivity) },
                                )
                            }
                        }

                        composable(ServiceNode.DomainAssignment.route) {
                            withOwner(viewModelStoreOwner.current) {
                                DomainAssignmentScreen()
                            }
                        }

                        composable(ServiceNode.AdvancedSettings.route) {
                            withOwner(viewModelStoreOwner.current) {
                                AdvancedSettingsScreen()
                            }
                        }

                        composable(ServiceNode.ChangeBrand.route) {
                            withOwner(viewModelStoreOwner.current) {
                                ChangeBrandScreen(
                                    onRequestIconClick = { navController.navigate(ServiceNode.RequestIcon.route) }
                                )
                            }
                        }

                        composable(ServiceNode.ChangeLabel.route) {
                            withOwner(viewModelStoreOwner.current) {
                                ChangeLabelScreen()
                            }
                        }

                        composable(ServiceNode.RequestIcon.route) {
                            withOwner(viewModelStoreOwner.current) {
                                RequestIconScreen()
                            }
                        }

                        composable(ServiceNode.Delete.route) {
                            withOwner(viewModelStoreOwner.current) {
                                DeleteServiceScreen()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_KEY_AUTH_SERVICE && resultCode == Activity.RESULT_OK) {
            viewModel.secretAuthenticated()
            return
        }
    }
}
