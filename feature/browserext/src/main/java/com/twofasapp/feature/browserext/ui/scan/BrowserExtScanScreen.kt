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
import com.twofasapp.android.navigation.Navigator
import com.twofasapp.android.navigation.Screen
import com.twofasapp.core.design.foundation.button.TextButton
import com.twofasapp.core.design.foundation.dialog.InfoDialog
import com.twofasapp.core.design.foundation.dialog.InputDialog
import com.twofasapp.core.design.foundation.dialog.InputValidation
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.feature.qrscan.QrScan
import com.twofasapp.feature.qrscan.QrScanFinder
import com.twofasapp.locale.MdtLocale
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
internal fun BrowserExtScanScreen(
    viewModel: BrowserExtScanViewModel = koinViewModel(),
    navigator: Navigator = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        uiState = uiState,
        onScanned = { viewModel.scanned(it) },
        onEventConsumed = { viewModel.consumeEvent(it) },
        onSuccess = { extensionId ->
            navigator.popTo(Screen.BrowserExt)
            navigator.open(Screen.BrowserExtPairing(extensionId = extensionId))
        },
    )
}

@Composable
private fun Content(
    uiState: BrowserExtScanUiState,
    onScanned: (String) -> Unit = {},
    onEventConsumed: (BrowserExtScanUiEvent) -> Unit = {},
    onSuccess: (String) -> Unit = {},
) {
    val strings = MdtLocale.strings
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
            TopAppBar(
                title = strings.scanQr,
                actions = {
                    TextButton(
                        text = strings.browserPairManuallyCta,
                        onClick = { showManualDialog = true },
                    )
                },
            )
        },
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
            label = strings.browserPairManuallyHint,
            positive = strings.commonOk,
            negative = strings.commonCancel,
            validate = { if (it.isNotBlank()) InputValidation.Valid else InputValidation.Invalid(null) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                keyboardType = KeyboardType.Text,
            ),
            onPositive = { onSuccess(it.trim().lowercase()) },
        )
    }

    if (showUnsupportedFormatError) {
        InfoDialog(
            onDismissRequest = {
                showUnsupportedFormatError = false
                qrScanEnabled = true
            },
            title = strings.commonError,
            body = strings.browserErrorScanFormatMsg,
        )
    }

    if (showUnknownError) {
        InfoDialog(
            onDismissRequest = {
                showUnknownError = false
                qrScanEnabled = true
            },
            title = strings.commonError,
            body = strings.browserErrorScanMsg,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        Content(
            uiState = BrowserExtScanUiState(),
        )
    }
}