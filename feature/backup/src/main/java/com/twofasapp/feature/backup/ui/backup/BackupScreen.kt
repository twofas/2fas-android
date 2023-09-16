package com.twofasapp.feature.backup.ui.backup

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.settings.SettingsDivider
import com.twofasapp.designsystem.settings.SettingsHeader
import com.twofasapp.designsystem.settings.SettingsLink
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun BackupScreen(
    viewModel: BackupViewModel = koinViewModel(),
    openSettings: () -> Unit,
    openExport: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackupScreenContent(
        uiState = uiState,
        onSettingsClick = openSettings,
        onExportClick = openExport,
    )
}

@Composable
private fun BackupScreenContent(
    uiState: BackupUiState,
    onSettingsClick: () -> Unit = {},
    onExportClick: () -> Unit = {},
) {
    Scaffold(
        topBar = { TwTopAppBar(titleText = TwLocale.strings.backupTitle) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
        ) {
            item { SettingsHeader(title = TwLocale.strings.backupDriveHeader) }

            item {
                SettingsLink(
                    title = TwLocale.strings.backupSyncSettings,
                    icon = TwIcons.Settings,
                    onClick = onSettingsClick,
                )
            }

            item { SettingsDivider() }

            item { SettingsHeader(title = TwLocale.strings.backupLocalHeader) }

            item {
                SettingsLink(
                    title = TwLocale.strings.backupImportFile,
                    icon = TwIcons.Import,
                    onClick = {},
                )
            }

            item {
                SettingsLink(
                    title = TwLocale.strings.backupExportFile,
                    subtitle = TwLocale.strings.backupExportFileDescription,
                    icon = TwIcons.Export,
                    enabled = uiState.exportEnabled,
                    onClick = onExportClick,
                )
            }

            if (uiState.debuggable) {
                item { SettingsDivider() }
                item { SettingsHeader(title = "Debug") }
                item {
                    SettingsLink(
                        title = "Show Google Drive file",
                        icon = TwIcons.Placeholder,
                        onClick = {},
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    BackupScreenContent(
        uiState = BackupUiState()
    )
}