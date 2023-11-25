package com.twofasapp.feature.backup.ui.backupsettings

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.data.services.domain.CloudSyncStatus
import com.twofasapp.data.services.domain.CloudSyncTrigger
import com.twofasapp.designsystem.R
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.dialog.InfoDialog
import com.twofasapp.designsystem.dialog.PasswordDialog
import com.twofasapp.designsystem.dialog.RichConfirmDialog
import com.twofasapp.designsystem.ktx.ConnectionState
import com.twofasapp.designsystem.ktx.currentConnectivityState
import com.twofasapp.designsystem.ktx.strings
import com.twofasapp.designsystem.settings.SettingsDivider
import com.twofasapp.designsystem.settings.SettingsHeader
import com.twofasapp.designsystem.settings.SettingsLink
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun BackupSettingsScreen(
    viewModel: BackupSettingsViewModel = koinViewModel(),
    goBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackupSettingsScreenContent(
        uiState = uiState,
        onSetPassword = { viewModel.setPassword(it) },
        onRemovePassword = { viewModel.removePassword(it) },
        onDeleteBackup = { viewModel.deleteBackup(it) },
        onFinish = { goBack() },
        onEventConsumed = { viewModel.consumeEvent(it) }
    )
}

@Composable
private fun BackupSettingsScreenContent(
    uiState: BackupSettingsUiState,
    onSetPassword: (String) -> Unit = {},
    onRemovePassword: (String) -> Unit = {},
    onDeleteBackup: (String?) -> Unit = {},
    onFinish: () -> Unit = {},
    onEventConsumed: (BackupSettingsUiEvent) -> Unit = {},
) {
    val context = LocalContext.current
    val strings = LocalContext.strings
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    var showConnectionErrorDialog by remember { mutableStateOf(false) }
    var showSetPasswordDialog by remember { mutableStateOf(false) }
    var showRemovePasswordDialog by remember { mutableStateOf(false) }
    var showWipePasswordDialog by remember { mutableStateOf(false) }
    var showPasswordError by remember { mutableStateOf(false) }

    uiState.events.firstOrNull()?.let { event ->
        LaunchedEffect(Unit) {
            when (event) {
                BackupSettingsUiEvent.Finish -> onFinish()
                BackupSettingsUiEvent.ShowWipePasswordDialogError -> {
                    showWipePasswordDialog = true
                    showPasswordError = true
                }

                BackupSettingsUiEvent.ShowRemovePasswordDialogError -> {
                    showRemovePasswordDialog = true
                    showPasswordError = true
                }
            }
        }

        onEventConsumed(event)
    }

    Scaffold(
        topBar = { TwTopAppBar(titleText = strings.backupSettingsTitle) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
        ) {
            if (uiState.encrypted || uiState.pass.isNullOrBlank().not()) {
                item {
                    SettingsLink(
                        title = strings.backupSettingsRemovePasswordTitle,
                        subtitle = strings.backupSettingsRemovePasswordMsg,
                        icon = TwIcons.LockOpen,
                        enabled = uiState.syncStatus != CloudSyncStatus.Syncing,
                        onClick = { showRemovePasswordDialog = true },
                    )
                }
            } else {
                item {
                    SettingsLink(
                        title = strings.backupSettingsSetPasswordTitle,
                        subtitle = strings.backupSettingsSetPasswordMsg,
                        icon = TwIcons.Lock,
                        enabled = uiState.syncStatus != CloudSyncStatus.Syncing,
                        onClick = { showSetPasswordDialog = true },
                    )
                }
            }

            if (uiState.syncActive) {
                item {
                    SettingsLink(
                        title = strings.backupSettingsDeleteBackupTitle,
                        subtitle = strings.backupSettingsDeleteBackupMsg,
                        icon = TwIcons.Delete,
                        enabled = uiState.syncStatus != CloudSyncStatus.Syncing,
                        onClick = { showConfirmDeleteDialog = true },
                    )
                }


                item {
                    SettingsDivider()
                }

                item {
                    SettingsHeader(title = strings.commonInfo)
                }

                item {
                    SettingsLink(
                        title = strings.backupSettingsAccountTitle,
                        subtitle = uiState.account,
                    )
                }

                item {
                    SettingsLink(
                        title = strings.backupSettingsSyncTitle,
                        subtitle = when (uiState.syncStatus) {
                            is CloudSyncStatus.Syncing -> strings.backupSyncStatusSyncing
                            is CloudSyncStatus.Default -> strings.backupSyncStatusWaiting
                            is CloudSyncStatus.Synced -> {
                                if (uiState.lastSyncMillis == 0L) {
                                    strings.backupSyncStatusWaiting
                                } else {
                                    TwLocale.formatDuration(millis = uiState.lastSyncMillis)
                                }
                            }

                            is CloudSyncStatus.Error -> {
                                when (uiState.syncStatus.trigger) {
                                    CloudSyncTrigger.SetPassword,
                                    CloudSyncTrigger.RemovePassword -> {
                                        if (uiState.lastSyncMillis == 0L) {
                                            strings.backupSyncStatusWaiting
                                        } else {
                                            TwLocale.formatDuration(millis = uiState.lastSyncMillis)
                                        }
                                    }

                                    else -> strings.backupSyncStatusError
                                }
                            }
                        },
                    )
                }
            }
        }

        if (showConfirmDeleteDialog) {
            RichConfirmDialog(
                onDismissRequest = { showConfirmDeleteDialog = false },
                image = painterResource(id = R.drawable.illustration_delete_confirm),
                title = strings.backupDeleteConfirmTitle,
                body = strings.backupDeleteConfirmMsg,
                positive = strings.commonDelete,
                negative = strings.commonCancel,
                onPositive = {
                    when (context.currentConnectivityState) {
                        ConnectionState.Available -> {
                            if (uiState.encrypted) {
                                showWipePasswordDialog = true
                            } else {
                                onDeleteBackup(null)
                            }
                        }

                        ConnectionState.Unavailable -> showConnectionErrorDialog = true
                    }
                }
            )
        }

        if (showConnectionErrorDialog) {
            InfoDialog(
                onDismissRequest = { showConnectionErrorDialog = false },
                title = strings.backupDeleteInternetErrorTitle,
                body = strings.backupDeleteInternetErrorMsg,
            )
        }

        if (showWipePasswordDialog) {
            PasswordDialog(
                onDismissRequest = {
                    showWipePasswordDialog = false
                    showPasswordError = false
                },
                confirmRequired = false,
                title = strings.backupDeleteEnterPasswordTitle,
                body = strings.backupDeleteEnterPasswordMsg,
                positive = strings.commonContinue,
                error = if (showPasswordError) strings.backupIncorrectPassword else null,
                onPositive = { onDeleteBackup(it) }
            )
        }

        if (showSetPasswordDialog) {
            PasswordDialog(
                onDismissRequest = {
                    showSetPasswordDialog = false
                    showPasswordError = false
                },
                title = strings.backupSetCloudPasswordTitle,
                body = strings.backupSetCloudPasswordMsg,
                positive = strings.commonContinue,
                onPositive = { onSetPassword(it) }
            )
        }

        if (showRemovePasswordDialog) {
            PasswordDialog(
                onDismissRequest = {
                    showRemovePasswordDialog = false
                    showPasswordError = false
                },
                confirmRequired = false,
                title = strings.backupRemoveCloudPasswordTitle,
                body = strings.backupRemoveCloudPasswordMsg,
                positive = strings.commonContinue,
                error = if (showPasswordError) strings.backupIncorrectPassword else null,
                onPositive = { onRemovePassword(it) },
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    BackupSettingsScreenContent(
        uiState = BackupSettingsUiState(
            account = "mail@test.com",
            syncActive = true,
        ),
    )
}