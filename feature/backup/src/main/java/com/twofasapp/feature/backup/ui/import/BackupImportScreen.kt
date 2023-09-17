package com.twofasapp.feature.backup.ui.import

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.designsystem.R
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwButton
import com.twofasapp.designsystem.common.TwCircularProgressIndicator
import com.twofasapp.designsystem.common.TwTextButton
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.dialog.PasswordDialog
import com.twofasapp.designsystem.ktx.strings
import com.twofasapp.designsystem.ktx.toastLong
import com.twofasapp.designsystem.ktx.toastShort
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun BackupImportScreen(
    viewModel: BackupImportViewModel = koinViewModel(),
    goBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        // Or fallback to ACTION_GET_CONTENT
        uri?.let { viewModel.fileOpened(it) } ?: goBack()
    }

    ScreenContent(
        uiState = uiState,
        onShowFilePicker = { launcher.launch(arrayOf("*/*")) },
        onPasswordConfirm = { viewModel.import(it) },
        onImportClick = { viewModel.import() },
        onEventConsumed = { viewModel.consumeEvent(it) },
        onGoBack = goBack,
    )
}

@Composable
private fun ScreenContent(
    uiState: BackupImportUiState,
    onShowFilePicker: () -> Unit = {},
    onImportClick: () -> Unit = {},
    onPasswordConfirm: (String) -> Unit = {},
    onEventConsumed: (BackupImportUiEvent) -> Unit = {},
    onGoBack: () -> Unit = {},
) {
    val context = LocalContext.current
    val strings = LocalContext.strings
    var showPasswordDialog by remember { mutableStateOf(false) }
    var passwordDialogError by remember { mutableStateOf("") }

    uiState.events.firstOrNull()?.let { event ->
        LaunchedEffect(Unit) {
            when (event) {
                BackupImportUiEvent.ShowFilePicker -> onShowFilePicker()
                BackupImportUiEvent.WrongPassword -> {
                    showPasswordDialog = true
                    passwordDialogError = strings.backupIncorrectPassword
                }

                BackupImportUiEvent.DecryptError -> {
                    showPasswordDialog = true
                    passwordDialogError = strings.backupImportErrorDecryptError
                }

                BackupImportUiEvent.ImportSuccess -> {
                    context.toastShort(strings.backupImportSuccess)
                    onGoBack()
                }

                is BackupImportUiEvent.ImportError -> context.toastLong(event.msg.orEmpty())
            }
        }

        onEventConsumed(event)
    }

    Scaffold(
        topBar = { TwTopAppBar(titleText = strings.backupImportFile) }
    ) { padding ->

        if (uiState.screenState != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp)
                        .animateContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
                ) {
                    Image(
                        painter = when (uiState.screenState) {
                            is ScreenState.BackupRead,
                            is ScreenState.BackupReadEncrypted -> painterResource(id = R.drawable.illustration_2fas_import)

                            is ScreenState.ErrorInvalidFile,
                            is ScreenState.ErrorInvalidFileSize -> painterResource(id = R.drawable.illustration_file_error)
                        },
                        contentDescription = null,
                        modifier = Modifier.height(124.dp)
                    )

                    Text(
                        text = when (uiState.screenState) {
                            is ScreenState.BackupRead -> strings.backupImportHeader
                            is ScreenState.BackupReadEncrypted -> strings.backupImportHeader
                            is ScreenState.ErrorInvalidFile -> strings.backupImportErrorHeader
                            is ScreenState.ErrorInvalidFileSize -> strings.backupImportErrorHeader
                        },
                        textAlign = TextAlign.Center,
                        color = TwTheme.color.onSurfacePrimary,
                        style = TwTheme.typo.title,
                    )

                    Text(
                        text = when (uiState.screenState) {
                            is ScreenState.BackupRead -> strings.backupImportMsg1
                            is ScreenState.BackupReadEncrypted -> strings.backupImportMsg1Encrypted
                            is ScreenState.ErrorInvalidFile -> strings.backupImportErrorMsg
                            is ScreenState.ErrorInvalidFileSize -> strings.backupImportErrorMsgSize
                        },
                        textAlign = TextAlign.Center,
                        color = TwTheme.color.onSurfacePrimary,
                        style = TwTheme.typo.body3,
                    )

                    when (uiState.screenState) {
                        is ScreenState.BackupRead -> {
                            Text(
                                text = uiState.screenState.servicesToImport.toString(),
                                textAlign = TextAlign.Center,
                                color = TwTheme.color.onSurfacePrimary,
                                style = TwTheme.typo.title,
                            )
                        }

                        is ScreenState.BackupReadEncrypted -> Unit
                        is ScreenState.ErrorInvalidFile -> Unit
                        is ScreenState.ErrorInvalidFileSize -> Unit
                    }

                    when (uiState.screenState) {
                        is ScreenState.BackupRead,
                        is ScreenState.BackupReadEncrypted -> {
                            Text(
                                text = strings.backupImportMsg2,
                                textAlign = TextAlign.Center,
                                color = TwTheme.color.onSurfacePrimary,
                                style = TwTheme.typo.body3,
                            )
                        }

                        is ScreenState.ErrorInvalidFile -> Unit
                        is ScreenState.ErrorInvalidFileSize -> Unit
                    }

                    if (uiState.importing) {
                        TwCircularProgressIndicator()
                    }
                }

                when (uiState.screenState) {
                    is ScreenState.BackupRead -> {
                        TwButton(
                            text = strings.backupImportCta,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .align(Alignment.CenterHorizontally),
                            onClick = { onImportClick() },
                            enabled = uiState.importing.not(),
                        )
                    }

                    is ScreenState.BackupReadEncrypted -> {
                        TwButton(
                            text = strings.backupImportCta,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .align(Alignment.CenterHorizontally),
                            onClick = { showPasswordDialog = true },
                        )
                    }

                    is ScreenState.ErrorInvalidFile,
                    is ScreenState.ErrorInvalidFileSize -> {
                        TwButton(
                            text = strings.backupImportChooseAnotherFileCta,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .align(Alignment.CenterHorizontally),
                            onClick = onShowFilePicker,
                        )
                    }
                }

                TwTextButton(
                    text = strings.commonCancel,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp, bottom = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    onClick = onGoBack,
                )
            }
        }
    }

    if (showPasswordDialog) {
        PasswordDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = strings.backupEnterPassword,
            body = strings.backupEnterPasswordDescription,
            positive = strings.commonContinue,
            error = passwordDialogError,
            confirmRequired = false,
            onPositive = { onPasswordConfirm(it) },
        )
    }
}


@Preview
@Composable
private fun Preview() {
    ScreenContent(
        uiState = BackupImportUiState(),
    )
}