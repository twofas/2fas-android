package com.twofasapp.feature.backup.ui.backup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.common.TwSwitch
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.dialog.InfoDialog
import com.twofasapp.designsystem.ktx.strings
import com.twofasapp.designsystem.settings.SettingsDivider
import com.twofasapp.designsystem.settings.SettingsHeader
import com.twofasapp.designsystem.settings.SettingsLink
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun BackupScreen(
    viewModel: BackupViewModel = koinViewModel(),
    openSettings: () -> Unit,
    openExport: () -> Unit,
    openImport: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScreenContent(
        uiState = uiState,
        onTurnOnSync = { viewModel.turnOnSync() },
        onTurnOffSync = { viewModel.turnOffSync() },
        onSettingsClick = openSettings,
        onExportClick = openExport,
        onImportClick = openImport,
        onEventConsumed = { viewModel.consumeEvent(it) },
        onSignInResult = { viewModel.handleSignInResult(it) }
    )
}

@Composable
private fun ScreenContent(
    uiState: BackupUiState,
    onTurnOnSync: () -> Unit = {},
    onTurnOffSync: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onExportClick: () -> Unit = {},
    onImportClick: () -> Unit = {},
    onEventConsumed: (BackupUiEvent) -> Unit = {},
    onSignInResult: (ActivityResult) -> Unit = {},
) {
    val strings = LocalContext.strings
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorDialogTitle by remember { mutableStateOf("") }
    var errorDialogMsg by remember { mutableStateOf("") }
    var showTurnOffConfirmationDialog by remember { mutableStateOf(false) }

    val signInLauncher = rememberLauncherForActivityResult(StartActivityForResult()) { onSignInResult(it) }

    uiState.events.firstOrNull()?.let { event ->
        LaunchedEffect(Unit) {
            when (event) {
                is BackupUiEvent.SignInToGoogle -> signInLauncher.launch(event.signInIntent)
                is BackupUiEvent.SignInPermissionError -> {
                    errorDialogTitle = strings.backupSignInPermissionErrorTitle
                    errorDialogMsg = strings.backupSignInPermissionErrorMsg
                    showErrorDialog = true
                }

                is BackupUiEvent.SignInNetworkError -> {
                    errorDialogTitle = strings.backupSignInInternetErrorTitle
                    errorDialogMsg = strings.backupSignInInternetErrorMsg
                    showErrorDialog = true
                }

                is BackupUiEvent.SignInUnknownError -> {
                    errorDialogTitle = strings.commonError
                    errorDialogMsg = event.msg.orEmpty()
                    showErrorDialog = true
                }
            }
        }

        onEventConsumed(event)
    }

    Scaffold(
        topBar = { TwTopAppBar(titleText = strings.backupTitle) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
        ) {
            item { SettingsHeader(title = strings.backupDriveHeader) }

            item {
                SettingsLink(
                    title = strings.backupSync,
                    icon = if (uiState.syncChecked) TwIcons.Cloud else TwIcons.CloudOff,
                    subtitle = if (uiState.syncChecked) null else strings.backupSyncDescription,
                    alignCenterIcon = false,
                    endContent = {
                        TwSwitch(
                            checked = uiState.syncChecked,
                            onCheckedChange = null,
                        )
                    },
                    onClick = {
                        if (uiState.syncChecked) {
                            showTurnOffConfirmationDialog = true
                        } else {
                            onTurnOnSync()
                        }
                    },
                )
            }

            item {
                SettingsLink(
                    title = strings.backupSyncSettings,
                    icon = TwIcons.Settings,
                    onClick = onSettingsClick,
                )
            }

            item { SettingsDivider() }

            item { SettingsHeader(title = strings.backupLocalHeader) }

            item {
                SettingsLink(
                    title = strings.backupImportFile,
                    icon = TwIcons.Import,
                    onClick = onImportClick,
                )
            }

            item {
                SettingsLink(
                    title = strings.backupExportFile,
                    subtitle = strings.backupExportFileDescription,
                    icon = TwIcons.Export,
                    enabled = uiState.exportEnabled,
                    onClick = onExportClick,
                )
            }
        }

        if (showTurnOffConfirmationDialog) {
            TurnOffConfirmationDialog(
                onDismissRequest = { showTurnOffConfirmationDialog = false },
                onConfirm = onTurnOffSync,
            )
        }

        if (showErrorDialog) {
            InfoDialog(
                onDismissRequest = { showErrorDialog = false },
                title = errorDialogTitle,
                body = errorDialogMsg
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ScreenContent(
        uiState = BackupUiState()
    )
}
