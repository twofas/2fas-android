package com.twofasapp.feature.browserext.ui.scan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.designsystem.common.TwTextButton
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.dialog.InfoDialog
import com.twofasapp.designsystem.dialog.InputDialog
import com.twofasapp.feature.qrscan.QrScan
import com.twofasapp.feature.qrscan.QrScanFinder
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun BrowserExtScanScreen(
    viewModel: BrowserExtScanViewModel = koinViewModel(),
    openProgress: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScreenContent(
        uiState = uiState,
        onScanned = { viewModel.scanned(it) },
        onEventConsumed = { viewModel.consumeEvent(it) },
        onSuccess = { openProgress(it) },
    )
}

@Composable
private fun ScreenContent(
    uiState: BrowserExtScanUiState,
    onScanned: (String) -> Unit = {},
    onEventConsumed: (BrowserExtScanUiEvent) -> Unit = {},
    onSuccess: (String) -> Unit = {},
) {
    val strings = TwLocale.strings
    var showManualDialog by remember { mutableStateOf(false) }
    var showUnsupportedFormatError by remember { mutableStateOf(false) }
    var showUnknownError by remember { mutableStateOf(false) }
    var qrScanEnabled by remember { mutableStateOf(true) }

    uiState.events.firstOrNull()?.let { event ->
        LaunchedEffect(Unit) {
            when (event) {
                BrowserExtScanUiEvent.ShowUnknownError -> showUnknownError = true
                BrowserExtScanUiEvent.ShowUnsupportedFormatError -> showUnsupportedFormatError = true
                is BrowserExtScanUiEvent.Success -> onSuccess(event.extensionId)
            }
        }

        onEventConsumed(event)
    }

    Scaffold(
        topBar = {
            TwTopAppBar(
                titleText = strings.scanQr,
                actions = {
                    TwTextButton(
                        text = strings.browserPairManuallyCta,
                        onClick = { showManualDialog = true },
                    )
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            QrScan(
                modifier = Modifier.fillMaxSize(),
                onScanned = {
                    if (qrScanEnabled) {
                        qrScanEnabled = false
                        onScanned(it)
                    }
                },
            )
            QrScanFinder()
        }
    }

    if (showManualDialog) {
        InputDialog(
            onDismissRequest = { showManualDialog = false },
            positive = strings.commonOk,
            negative = strings.commonCancel,
            hint = strings.browserPairManuallyHint,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                keyboardType = KeyboardType.Text,
            ),
            positiveEnabled = { it.isNotBlank() },
            onPositiveClick = { onSuccess(it.trim().lowercase()) },
            minLength = 1,
        )
    }

    if (showUnsupportedFormatError) {
        InfoDialog(
            onDismissRequest = {
                showUnsupportedFormatError = false
                qrScanEnabled = true
            },
            title = strings.commonError,
            body = strings.browserErrorScanFormatMsg
        )
    }

    if (showUnknownError) {
        InfoDialog(
            onDismissRequest = {
                showUnknownError = false
                qrScanEnabled = true
            },
            title = strings.commonError,
            body = strings.browserErrorScanMsg
        )
    }
}

@Preview
@Composable
private fun Preview() {
    ScreenContent(
        uiState = BrowserExtScanUiState()
    )
}
