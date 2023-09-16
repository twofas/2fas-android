package com.twofasapp.feature.backup.navigation

import androidx.compose.runtime.Composable
import com.twofasapp.feature.backup.ui.backup.BackupScreen
import com.twofasapp.feature.backup.ui.backupsettings.BackupSettingsScreen
import com.twofasapp.feature.backup.ui.export.BackupExportScreen

@Composable
fun BackupRoute(
    openSettings: () -> Unit,
    openExport: () -> Unit,
) {
    BackupScreen(
        openSettings = openSettings,
        openExport = openExport,
    )
}

@Composable
fun BackupSettingsRoute(
) {
    BackupSettingsScreen()
}

@Composable
fun BackupExportRoute(
    goBack: () -> Unit,
) {
    BackupExportScreen(goBack = goBack)
}