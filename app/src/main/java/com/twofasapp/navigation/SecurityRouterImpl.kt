package com.twofasapp.navigation

import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.twofasapp.navigation.base.withOwner
import com.twofasapp.security.ui.changepin.ChangePinScreenFactory
import com.twofasapp.security.ui.disablepin.DisablePinScreenFactory
import com.twofasapp.security.ui.security.SecurityScreenFactory
import com.twofasapp.security.ui.setuppin.SetupPinScreenFactory
import timber.log.Timber

class SecurityRouterImpl(
    private val securityScreenFactory: SecurityScreenFactory,
    private val setupPinScreenFactory: SetupPinScreenFactory,
    private val disablePinScreenFactory: DisablePinScreenFactory,
    private val changePinScreenFactory: ChangePinScreenFactory,
) : SecurityRouter() {

    companion object {
        private const val SECURITY = "security"
        private const val SETUP_PIN = "pin/setup"
        private const val DISABLE_PIN = "pin/disable"
        private const val CHANGE_PIN = "pin/change"
    }

    override fun buildNavGraph(builder: NavGraphBuilder, viewModelStoreOwner: ViewModelStoreOwner?) {
        builder.composable(route = SECURITY, content = {
            withOwner(viewModelStoreOwner) { securityScreenFactory.create() }
        })

        builder.composable(route = SETUP_PIN, content = {
            withOwner(viewModelStoreOwner) { setupPinScreenFactory.create() }
        })

        builder.composable(route = DISABLE_PIN, content = {
            withOwner(viewModelStoreOwner) { disablePinScreenFactory.create() }
        })

        builder.composable(route = CHANGE_PIN, content = {
            withOwner(viewModelStoreOwner) { changePinScreenFactory.create() }
        })
    }

    override fun startDirection(): String = SECURITY

    override fun navigate(
        navController: NavHostController,
        direction: SecurityDirections
    ) {
        Timber.d("$direction")

        when (direction) {
            SecurityDirections.GoBack -> navController.popBackStack()
            SecurityDirections.Security -> navController.navigate(SECURITY)
            SecurityDirections.SetupPin -> navController.navigate(SETUP_PIN) { popUpTo(SECURITY) }
            SecurityDirections.DisablePin -> navController.navigate(DISABLE_PIN)
            SecurityDirections.ChangePin -> navController.navigate(CHANGE_PIN)
        }
    }

    override fun navigateBack() {
        navigate(SecurityDirections.GoBack)
    }
}