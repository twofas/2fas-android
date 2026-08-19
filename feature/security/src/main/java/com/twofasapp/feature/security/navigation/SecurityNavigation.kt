package com.twofasapp.feature.security.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.twofasapp.android.navigation.LegacyScreen
import com.twofasapp.feature.security.ui.changepin.ChangePinScreen
import com.twofasapp.feature.security.ui.disablepin.DisablePinScreen
import com.twofasapp.feature.security.ui.security.SecurityScreen
import com.twofasapp.feature.security.ui.setuppin.SetupPinScreen

fun NavGraphBuilder.securityNavigation(
    navController: NavHostController,
) {
    composable(LegacyScreen.Security.route) {
        SecurityScreen(
            openSetupPin = { navController.navigate(LegacyScreen.SetupPin.route) { popUpTo(LegacyScreen.Security.route) } },
            openDisablePin = { navController.navigate(LegacyScreen.DisablePin.route) },
            openChangePin = { navController.navigate(LegacyScreen.ChangePin.route) },
        )
    }

    composable(LegacyScreen.SetupPin.route) {
        SetupPinScreen()
    }

    composable(LegacyScreen.DisablePin.route) {
        DisablePinScreen()
    }

    composable(LegacyScreen.ChangePin.route) {
        ChangePinScreen(
            openSetupPin = { navController.navigate(LegacyScreen.SetupPin.route) { popUpTo(LegacyScreen.Security.route) } },
        )
    }
}