package com.twofasapp.feature.backup.ui.export

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwButton
import com.twofasapp.designsystem.common.TwSwitch
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.dialog.ExportPasswordRegex
import com.twofasapp.designsystem.dialog.PasswordDialog
import com.twofasapp.designsystem.ktx.strings
import com.twofasapp.designsystem.ktx.toastShort
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
internal fun BackupExportScreen(
    viewModel: BackupExportViewModel = koinViewModel(),
    goBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("*/*")) { uri ->
        uri?.let { viewModel.downloadBackup(it) }
    }

    ScreenContent(
        uiState = uiState,
        onPasswordCheckedChange = { viewModel.togglePassword() },
        onPasswordConfirm = { viewModel.updatePassword(it) },
        onShareClick = { viewModel.shareBackup() },
        onDownloadClick = { launcher.launch(generateFilename()) },
        onEventConsumed = { viewModel.consumeEvent(it) },
        onGoBack = goBack,
    )
}

@Composable
private fun ScreenContent(
    uiState: BackupExportUiState,
    onPasswordCheckedChange: () -> Unit = {},
    onPasswordConfirm: (String) -> Unit = {},
    onShareClick: () -> Unit = {},
    onDownloadClick: () -> Unit = {},
    onEventConsumed: (BackupExportUiEvent) -> Unit = {},
    onGoBack: () -> Unit = {},
) {

    val context = LocalContext.current
    val strings = LocalContext.strings
    var showPasswordDialog by remember { mutableStateOf(false) }
    var exportMethod = ExportMethod.Download

    uiState.events.firstOrNull()?.let { event ->
        LaunchedEffect(Unit) {
            when (event) {
                is BackupExportUiEvent.ShowSharePicker -> context.showSharePicker(
                    appId = event.appId,
                    content = event.content,
                )

                BackupExportUiEvent.DownloadError -> context.toastShort(strings.backupDownloadError)
                BackupExportUiEvent.DownloadSuccess -> {
                    context.toastShort(strings.backupDownloadSuccess)
                    onGoBack()
                }

                BackupExportUiEvent.ShareError -> context.toastShort(strings.backupShareError)
            }
        }

        onEventConsumed(event)
    }

    Scaffold(
        topBar = { TwTopAppBar(titleText = TwLocale.strings.backupExportFile) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
            ) {
                Image(
                    painter = painterResource(id = com.twofasapp.designsystem.R.drawable.illustration_2fas_export),
                    contentDescription = null,
                    modifier = Modifier.height(124.dp)
                )

                Text(
                    text = TwLocale.strings.backupExportHeader,
                    textAlign = TextAlign.Center,
                    color = TwTheme.color.onSurfacePrimary,
                    style = TwTheme.typo.title,
                )

                Text(
                    text = TwLocale.strings.backupExportMsg,
                    textAlign = TextAlign.Center,
                    color = TwTheme.color.onSurfacePrimary,
                    style = TwTheme.typo.body3,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TwSwitch(
                        checked = uiState.passwordChecked,
                        onCheckedChange = { onPasswordCheckedChange() },
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = TwLocale.strings.backupExportPassMsg,
                        color = TwTheme.color.onSurfacePrimary,
                        style = TwTheme.typo.body3,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TwButton(
                    text = TwLocale.strings.backupExportShareCta,
                    leadingIcon = TwIcons.Share,
                    leadingIconTint = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        exportMethod = ExportMethod.Share

                        if (uiState.passwordChecked) {
                            showPasswordDialog = true
                        } else {
                            onShareClick()
                        }
                    },
                )
                TwButton(
                    text = TwLocale.strings.backupExportCta,
                    leadingIcon = TwIcons.Download,
                    leadingIconTint = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        exportMethod = ExportMethod.Download

                        if (uiState.passwordChecked) {
                            showPasswordDialog = true
                        } else {
                            onDownloadClick()
                        }
                    },
                )
            }

            if (showPasswordDialog) {
                PasswordDialog(
                    onDismissRequest = { showPasswordDialog = false },
                    title = TwLocale.strings.backupSetPassword,
                    body = TwLocale.strings.backupSetPasswordDescription,
                    validation = { text -> ExportPasswordRegex.matches(text) },
                    validationHint = TwLocale.strings.backupPasswordValidationHint,
                    onPositive = {
                        onPasswordConfirm(it)

                        when (exportMethod) {
                            ExportMethod.Share -> onShareClick()
                            ExportMethod.Download -> onDownloadClick()
                        }
                    },
                )
            }
        }
    }
}

private fun Context.showSharePicker(
    appId: String,
    content: String,
) {
    val filename = generateFilename()
    val backupDir = File(getExternalFilesDir(null), "backup")

    backupDir.mkdir()

    val file = File(backupDir, filename)
    val outputStream = FileOutputStream(file)
    outputStream.write(content.toByteArray())
    outputStream.close()

    val uri = FileProvider.getUriForFile(this, appId, file)

    val shareIntent = Intent().apply {
        type = "*/*"
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, filename)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    startActivity(
        Intent.createChooser(
            shareIntent,
            "2FAS Backup File",
        )
    )
}

private fun generateFilename() =
    "2fas-backup-${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))}.2fas"

private enum class ExportMethod {
    Share, Download
}

@Preview
@Composable
private fun Preview() {
    ScreenContent(
        uiState = BackupExportUiState(),
    )
}
