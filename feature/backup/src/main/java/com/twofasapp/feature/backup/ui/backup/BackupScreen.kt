package com.twofasapp.feature.backup.ui.backup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.data.services.domain.CloudSyncError
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwSwitch
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.dialog.InfoDialog
import com.twofasapp.designsystem.dialog.PasswordDialog
import com.twofasapp.designsystem.ktx.strings
import com.twofasapp.designsystem.settings.SettingsDivider
import com.twofasapp.designsystem.settings.SettingsHeader
import com.twofasapp.designsystem.settings.SettingsLink
import com.twofasapp.locale.R
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
        onEnterPassword = { viewModel.enterPassword(it) },
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
    onEnterPassword: (String) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onExportClick: () -> Unit = {},
    onImportClick: () -> Unit = {},
    onEventConsumed: (BackupUiEvent) -> Unit = {},
    onSignInResult: (ActivityResult) -> Unit = {},
) {
    val strings = LocalContext.strings
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showPasswordError by remember { mutableStateOf(false) }
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

                is BackupUiEvent.ShowPasswordDialogError -> {
                    showPasswordDialog = true
                    showPasswordError = true
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
                    subtitle = if (uiState.showSyncMsg) strings.backupSyncDescription else null,
                    alignCenterIcon = false,
                    enabled = uiState.syncEnabled,
                    endContent = {
                        TwSwitch(
                            checked = uiState.syncChecked,
                            onCheckedChange = null,
                        )
                    },
                    onClick = {
                        if (uiState.syncChecked) {
                            showTurnOffConfirmationDialog = true
                        } else if (uiState.showError && (uiState.error == CloudSyncError.DecryptWrongPassword || uiState.error == CloudSyncError.DecryptNoPassword)) {
                            showPasswordDialog = true
                        } else {
                            onTurnOnSync()
                        }
                    },
                )
            }

            if (uiState.showError) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 68.dp, end = 16.dp, bottom = 16.dp, top = 8.dp)
                            .border(1.dp, TwTheme.color.primary, TwTheme.shape.roundedDefault)
                            .padding(16.dp),

                        ) {

                        Text(
                            text = stringResource(id = formatErrorMsg(uiState.error ?: CloudSyncError.Unknown)),
                            style = TwTheme.typo.body3,
                            color = TwTheme.color.primary,
                        )


                        if (formatShouldShowErrorCode(uiState.error ?: CloudSyncError.Unknown)) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Error code: ${uiState.error?.code}",
                                style = TwTheme.typo.caption,
                                color = TwTheme.color.primary,
                            )
                        }
                    }
                }
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

        if (showPasswordDialog) {
            PasswordDialog(
                onDismissRequest = {
                    showPasswordDialog = false
                    showPasswordError = false
                },
                confirmRequired = false,
                title = strings.backupEnterCloudPasswordTitle,
                body = strings.backupEnterCloudPasswordMsg,
                error = if (showPasswordError) strings.backupIncorrectPassword else null,
                positive = strings.commonContinue,
                onPositive = { onEnterPassword(it) }
            )
        }
    }
}

private fun formatErrorMsg(type: CloudSyncError): Int =
    when (type) {
        CloudSyncError.GooglePlayServicesUnavailable,
        CloudSyncError.JsonParsingFailure,
        CloudSyncError.SyncFailure,
        CloudSyncError.HttpApiFailure,
        CloudSyncError.FileNotFound,
        CloudSyncError.Unknown -> R.string.backup_error_unknown

        CloudSyncError.NetworkUnavailable -> R.string.backup_error_network

        CloudSyncError.GoogleUserPermissionDenied,
        CloudSyncError.CredentialsNotFound,
        CloudSyncError.GoogleAuthFailure -> R.string.backup_error_auth

        CloudSyncError.EncryptUnknownFailure -> R.string.backup_error_encrypt_unknown
        CloudSyncError.DecryptNoPassword -> R.string.backup_error_no_password
        CloudSyncError.DecryptWrongPassword -> R.string.backup_error_wrong_password
        CloudSyncError.DecryptUnknownFailure -> R.string.backup_error_decrypt_unknown
    }

private fun formatShouldShowErrorCode(type: CloudSyncError) =
    when (type) {
        CloudSyncError.DecryptWrongPassword,
        CloudSyncError.DecryptNoPassword -> false

        else -> true
    }

@Preview
@Composable
private fun Preview() {
    ScreenContent(
        uiState = BackupUiState()
    )
}
