package com.twofasapp.feature.home.ui.services.add

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.twofasapp.android.navigation.NavAnimation
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.designsystem.common.Modal
import com.twofasapp.feature.home.ui.services.add.manual.AddServiceManualScreen
import com.twofasapp.feature.home.ui.services.add.scan.AddServiceScanScreen
import com.twofasapp.feature.home.ui.services.add.success.AddServiceSuccessScreen

internal object NavArg {
    val ServiceId = navArgument("id") { type = NavType.LongType }
}

@Composable
fun AddServiceModal(
    onAddedSuccessfully: (RecentlyAddedService) -> Unit = {},
) {
    val navController = rememberNavController()

    Modal {
        NavHost(
            navController = navController,
            startDestination = "main",
            enterTransition = NavAnimation.Enter,
            exitTransition = NavAnimation.Exit,
        ) {

            composable(route = "main") {
                AddServiceScanScreen(
                    openManual = { navController.navigate("manual") },
                    onAddedSuccessfully = {
                        onAddedSuccessfully(it)
                        navController.navigate("success/${it.serviceId}") { popUpTo(0) { inclusive = true } }
                    },
                )
            }

            composable(route = "manual") {
                AddServiceManualScreen(
                    onAddedSuccessfully = {
                        onAddedSuccessfully(it)
                        navController.navigate("success/${it.serviceId}") { popUpTo(0) { inclusive = true } }
                    },
                )
            }

            composable(
                route = "success/{${NavArg.ServiceId.name}}",
                arguments = listOf(NavArg.ServiceId)
            ) {
                AddServiceSuccessScreen()
            }
        }
    }
}