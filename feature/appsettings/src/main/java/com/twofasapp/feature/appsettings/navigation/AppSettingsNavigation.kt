package com.twofasapp.feature.appsettings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.twofasapp.android.navigation.NavGraph
import com.twofasapp.feature.appsettings.ui.AppSettingsRoute

object AppSettingsGraph : com.twofasapp.android.navigation.NavGraph {
    override val route: String = "appsettings"
}

fun NavGraphBuilder.appSettingsNavigation() {
    composable(
        AppSettingsGraph.route,
    ) {
        AppSettingsRoute()
    }
}