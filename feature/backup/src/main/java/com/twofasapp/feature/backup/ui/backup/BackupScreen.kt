package com.twofasapp.feature.backup.ui.backup

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun BackupScreen(
    viewModel: BackupViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackupScreenContent(
        uiState = uiState,
    )
}

@Composable
private fun BackupScreenContent(
    uiState: BackupUiState,
) {
    Scaffold(
        topBar = { TwTopAppBar(titleText = TwLocale.strings.backupTitle) }
    ) { padding ->

    }
}

@Preview
@Composable
private fun Preview() {
    BackupScreenContent(
        uiState = BackupUiState()
    )
}