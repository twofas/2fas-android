package com.twofasapp.feature.startup.navigation

import androidx.compose.runtime.Composable
import com.twofasapp.feature.startup.ui.startup.StartupScreen

@Composable
fun StartupRoute(
    openHome: () -> Unit = {},
    openBackup: () -> Unit = {},
) {
    StartupScreen(
        openHome = openHome,
        openBackup = openBackup,
    )
}
