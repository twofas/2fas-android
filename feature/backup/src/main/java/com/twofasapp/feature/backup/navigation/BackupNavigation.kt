package com.twofasapp.feature.backup.navigation

import androidx.compose.runtime.Composable
import com.twofasapp.feature.backup.ui.backup.BackupScreen
import com.twofasapp.feature.backup.ui.backupsettings.BackupSettingsScreen
import com.twofasapp.feature.backup.ui.export.BackupExportScreen
import com.twofasapp.feature.backup.ui.import.BackupImportScreen

@Composable
fun BackupRoute(
    openSettings: () -> Unit,
    openExport: () -> Unit,
    openImport: () -> Unit,
) {
    BackupScreen(
        openSettings = openSettings,
        openExport = openExport,
        openImport = openImport,
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

@Composable
fun BackupImportRoute(
    goBack: () -> Unit,
) {
    BackupImportScreen(goBack = goBack)
}