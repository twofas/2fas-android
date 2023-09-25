package com.twofasapp.feature.startup.navigation

import androidx.compose.runtime.Composable
import com.twofasapp.feature.startup.ui.StartupScreen

@Composable
fun StartupRoute(
    openHome: () -> Unit
) {
    StartupScreen(openHome = openHome)
}
