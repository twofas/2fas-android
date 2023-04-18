package com.twofasapp.feature.appsettings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.twofasapp.common.navigation.NavGraph
import com.twofasapp.feature.appsettings.ui.AppSettingsRoute

object AppSettingsGraph : NavGraph {
    override val route: String = "appsettings"
}

fun NavGraphBuilder.appSettingsNavigation() {
    composable(
        AppSettingsGraph.route,
    ) {
        AppSettingsRoute()
    }
}