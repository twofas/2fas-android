package com.twofasapp.feature.startup.navigation

import androidx.compose.runtime.Composable
import com.twofasapp.feature.startup.ui.startup.StartupScreen
import com.twofasapp.feature.startup.ui.startupbackup.StartupBackupScreen

@Composable
fun StartupRoute(
    openStartupBackup: () -> Unit
) {
    StartupScreen(openStartupBackup = openStartupBackup)
}

@Composable
fun StartupBackupRoute(
    openHome: () -> Unit,
    openBackup: () -> Unit,
) {
    StartupBackupScreen(
        openHome = openHome,
        openBackup = openBackup,
    )
}
