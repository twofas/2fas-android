package com.twofasapp.services.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.twofasapp.common.navigation.NavGraph
import com.twofasapp.services.ui.ServiceScreenRoute

object ServiceGraph : NavGraph {
    override val route: String = "service/{${ServiceNavArg.ServiceId.name}}"
}

object ServiceNavArg {
    val ServiceId = navArgument("id") {
        type = NavType.LongType
        defaultValue = 0L
    }
}

fun NavGraphBuilder.serviceNavigation(navController: NavController) {
    composable(ServiceGraph.route, arguments = listOf(ServiceNavArg.ServiceId)) {
        ServiceScreenRoute(navController)
    }
}