package com.twofasapp.navigation

import android.app.Activity
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.twofasapp.extensions.startActivity
import com.twofasapp.extensions.startActivityForResult
import com.twofasapp.navigation.base.withOwner
import com.twofasapp.security.ui.lock.LockActivity
import com.twofasapp.security.ui.security.SecurityActivity
import com.twofasapp.services.ui.ServiceActivity
import com.twofasapp.services.ui.advancedsettings.AdvancedSettingsScreenFactory
import com.twofasapp.services.ui.changebrand.ChangeBrandScreenFactory
import com.twofasapp.services.ui.changelabel.ChangeLabelScreenFactory
import com.twofasapp.services.ui.deleteservice.DeleteServiceScreenFactory
import com.twofasapp.services.ui.domainassignment.DomainAssignmentScreenFactory
import com.twofasapp.services.ui.requesticon.RequestIconScreenFactory
import com.twofasapp.services.ui.service.ServiceScreenFactory
import timber.log.Timber

class ServiceRouterImpl(
    private val serviceScreenFactory: ServiceScreenFactory,
    private val domainAssignmentScreenFactory: DomainAssignmentScreenFactory,
    private val advancedSettingsScreenFactory: AdvancedSettingsScreenFactory,
    private val changeBrandScreenFactory: ChangeBrandScreenFactory,
    private val changeLabelScreenFactory: ChangeLabelScreenFactory,
    private val requestIconScreenFactory: RequestIconScreenFactory,
    private val deleteServiceScreenFactory: DeleteServiceScreenFactory,
) : ServiceRouter() {

    companion object {
        private const val SERVICE = "service"
        private const val DOMAIN_ASSIGNMENT = "domain_assignment"
        private const val ADVANCED = "advanced_settings"
        private const val CHANGE_BRAND = "change_brand"
        private const val CHANGE_LABEL = "change_label"
        private const val REQUEST_ICON = "request_icon"
        private const val DELETE_SERVICE = "delete_service"
    }

    override fun buildNavGraph(builder: NavGraphBuilder, viewModelStoreOwner: ViewModelStoreOwner?) {
        builder.composable(route = SERVICE, content = {
            withOwner(viewModelStoreOwner) { serviceScreenFactory.create() }
        })

        builder.composable(route = DOMAIN_ASSIGNMENT, content = {
            withOwner(viewModelStoreOwner) { domainAssignmentScreenFactory.create() }
        })

        builder.composable(route = ADVANCED, content = {
            withOwner(viewModelStoreOwner) { advancedSettingsScreenFactory.create() }
        })

        builder.composable(route = CHANGE_BRAND, content = {
            withOwner(viewModelStoreOwner) { changeBrandScreenFactory.create() }
        })

        builder.composable(route = CHANGE_LABEL, content = {
            withOwner(viewModelStoreOwner) { changeLabelScreenFactory.create() }
        })

        builder.composable(route = REQUEST_ICON, content = {
            withOwner(viewModelStoreOwner) { requestIconScreenFactory.create() }
        })

        builder.composable(route = DELETE_SERVICE, content = {
            withOwner(viewModelStoreOwner) { deleteServiceScreenFactory.create() }
        })
    }

    override fun startDirection(): String = SERVICE

    override fun navigate(
        navController: NavHostController,
        direction: ServiceDirections,
    ) {
        Timber.d("$direction")

        when (direction) {
            ServiceDirections.GoBack -> navController.popBackStack()
            ServiceDirections.Service -> navController.navigate(SERVICE)
            ServiceDirections.Advanced -> navController.navigate(ADVANCED)
            ServiceDirections.ChangeBrand -> navController.navigate(CHANGE_BRAND)
            ServiceDirections.ChangeLabel -> navController.navigate(CHANGE_LABEL)
            ServiceDirections.RequestIcon -> navController.navigate(REQUEST_ICON)
            ServiceDirections.DomainAssignment -> navController.navigate(DOMAIN_ASSIGNMENT)
            ServiceDirections.Delete -> navController.navigate(DELETE_SERVICE)
            ServiceDirections.Security -> navController.context.startActivity<SecurityActivity>()
            ServiceDirections.AuthenticateSecret -> {
                (navController.context as Activity).startActivityForResult<LockActivity>(
                    ServiceActivity.REQUEST_KEY_AUTH_SERVICE, "canGoBack" to true
                )
            }
        }
    }

    override fun navigateBack() {
        navigate(ServiceDirections.GoBack)
    }
}