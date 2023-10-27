package com.twofasapp.feature.security.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.twofasapp.android.navigation.Screen
import com.twofasapp.feature.security.ui.changepin.ChangePinScreen
import com.twofasapp.feature.security.ui.disablepin.DisablePinScreen
import com.twofasapp.feature.security.ui.security.SecurityScreen
import com.twofasapp.feature.security.ui.setuppin.SetupPinScreen

fun NavGraphBuilder.securityNavigation(
    navController: NavHostController,
) {
    composable(Screen.Security.route) {
        SecurityScreen(
            openSetupPin = { navController.navigate(Screen.SetupPin.route) { popUpTo(Screen.Security.route) } },
            openDisablePin = { navController.navigate(Screen.DisablePin.route) },
            openChangePin = { navController.navigate(Screen.ChangePin.route) },
        )
    }

    composable(Screen.SetupPin.route) {
        SetupPinScreen()
    }

    composable(Screen.DisablePin.route) {
        DisablePinScreen()
    }

    composable(Screen.ChangePin.route) {
        ChangePinScreen(
            openSetupPin = { navController.navigate(Screen.SetupPin.route) { popUpTo(Screen.Security.route) } },
        )
    }
}