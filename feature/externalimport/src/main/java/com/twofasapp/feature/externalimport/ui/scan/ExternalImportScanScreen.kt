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
import com.twofasapp.android.navigation.Navigator
import com.twofasapp.android.navigation.Screen
import com.twofasapp.common.ktx.legacyEncodeBase64ToString
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.feature.externalimport.domain.ImportType
import com.twofasapp.feature.qrscan.QrScan
import com.twofasapp.feature.qrscan.QrScanFinder
import com.twofasapp.locale.MdtLocale
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
internal fun ExternalImportScanScreen(
    importType: ImportType,
    viewModel: ExternalImportScanViewModel = koinViewModel(),
    navigator: Navigator = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        uiState = uiState,
        onScanned = { scanned ->
            navigator.open(
                Screen.ExternalImportResult(
                    importType = importType.name,
                    importFileContent = scanned.legacyEncodeBase64ToString(),
                ),
            )
        },
    )
}

@Composable
private fun Content(
    uiState: ExternalImportScanUiState,
    onScanned: (String) -> Unit = {},
) {
    val strings = MdtLocale.strings
    var qrScanEnabled = true

    Scaffold(
        topBar = { TopAppBar(title = strings.scanQr) },
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
    PreviewTheme {
        Content(
            uiState = ExternalImportScanUiState(),
        )
    }
}