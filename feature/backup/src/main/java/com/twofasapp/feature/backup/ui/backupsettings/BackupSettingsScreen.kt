package com.twofasapp.feature.backup.ui.backupsettings

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun BackupSettingsScreen(
    viewModel: BackupSettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackupSettingsScreenContent(
        uiState = uiState,
    )
}

@Composable
private fun BackupSettingsScreenContent(
    uiState: BackupSettingsUiState,
) {
    Scaffold(
        topBar = { TwTopAppBar(titleText = TwLocale.strings.backupSettingsTitle) }
    ) { padding ->

    }
}

@Preview
@Composable
private fun Preview() {
    BackupSettingsScreenContent(
        uiState = BackupSettingsUiState(),
    )
}