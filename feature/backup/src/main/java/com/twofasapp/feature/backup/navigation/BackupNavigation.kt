package com.twofasapp.feature.backup.navigation

import androidx.compose.runtime.Composable
import com.twofasapp.feature.backup.ui.backup.BackupScreen
import com.twofasapp.feature.backup.ui.backupsettings.BackupSettingsScreen
import com.twofasapp.feature.backup.ui.export.BackupExportScreen
import com.twofasapp.feature.backup.ui.import.BackupImportScreen

@Composable
fun BackupRoute() {
    BackupScreen()
}

@Composable
fun BackupSettingsRoute() {
    BackupSettingsScreen()
}

@Composable
fun BackupExportRoute() {
    BackupExportScreen()
}

@Composable
fun BackupImportRoute(
    importFileUri: String? = null,
) {
    BackupImportScreen(importFileUri = importFileUri)
}