package com.twofasapp.feature.home.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.twofasapp.common.navigation.NavGraph

object ServicesGraph : NavGraph {
    override val route: String = "services"
}

fun NavGraphBuilder.servicesNavigation(
) {
    composable(ServicesGraph.route) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = "Services", modifier = Modifier.align(Alignment.Center))
        }
    }
}