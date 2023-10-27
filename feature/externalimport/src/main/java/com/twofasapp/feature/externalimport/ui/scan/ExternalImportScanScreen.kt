package com.twofasapp.feature.externalimport.ui.scan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.common.ktx.encodeBase64ToString
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.feature.qrscan.QrScan
import com.twofasapp.feature.qrscan.QrScanFinder
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun ExternalImportScanScreen(
    viewModel: ExternalImportScanViewModel = koinViewModel(),
    openResult: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScreenContent(
        uiState = uiState,
        onScanned = { openResult(it.encodeBase64ToString()) },
    )
}

@Composable
private fun ScreenContent(
    uiState: ExternalImportScanUiState,
    onScanned: (String) -> Unit = {},
) {
    val strings = TwLocale.strings
    var qrScanEnabled = true

    Scaffold(
        topBar = { TwTopAppBar(titleText = strings.scanQr) }
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
}

@Preview
@Composable
private fun Preview() {
    ScreenContent(
        uiState = ExternalImportScanUiState()
    )
}
