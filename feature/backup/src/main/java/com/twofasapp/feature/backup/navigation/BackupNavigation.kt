package com.twofasapp.feature.backup.navigation

import androidx.compose.runtime.Composable
import com.twofasapp.feature.backup.ui.backup.BackupScreen
import com.twofasapp.feature.backup.ui.backupsettings.BackupSettingsScreen

@Composable
fun BackupRoute(
) {
    BackupScreen()
}

@Composable
fun BackupSettingsRoute(
) {
    BackupSettingsScreen()
}