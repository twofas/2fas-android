package com.twofasapp.feature.home.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.twofasapp.common.navigation.NavGraph

object NotificationsGraph : NavGraph {
    override val route: String = "notifications"
}

fun NavGraphBuilder.notificationsNavigation(
) {
    composable(NotificationsGraph.route) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = "Notifications", modifier = Modifier.align(Alignment.Center))
        }
    }
}