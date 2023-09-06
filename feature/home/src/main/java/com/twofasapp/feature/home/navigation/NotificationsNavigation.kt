package com.twofasapp.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.twofasapp.android.navigation.NavGraph
import com.twofasapp.feature.home.ui.notifications.NotificationsRoute

object NotificationsGraph : NavGraph {
    override val route: String = "notifications"
}

fun NavGraphBuilder.notificationsNavigation() {
    composable(NotificationsGraph.route) {
        NotificationsRoute()
    }
}