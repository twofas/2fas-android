package com.twofasapp.feature.externalimport.navigation

import androidx.compose.runtime.Composable
import com.twofasapp.feature.externalimport.domain.ImportType
import com.twofasapp.feature.externalimport.ui.main.ExternalImportScreen
import com.twofasapp.feature.externalimport.ui.result.ExternalImportResultScreen
import com.twofasapp.feature.externalimport.ui.scan.ExternalImportScanScreen
import com.twofasapp.feature.externalimport.ui.selector.ExternalImportSelectorScreen

@Composable
fun ExternalImportSelectorRoute(
    openImport: (ImportType) -> Unit,
) {
    ExternalImportSelectorScreen(
        onImportTypeSelected = openImport
    )
}

@Composable
fun ExternalImportRoute(
    openScanner: () -> Unit,
    openResult: (String) -> Unit,
) {
    ExternalImportScreen(
        openScanner = openScanner,
        openResult = openResult,
    )
}

@Composable
fun ExternalImportScanRoute(
    openResult: (String) -> Unit,
) {
    ExternalImportScanScreen(
        openResult = openResult,
    )
}

@Composable
fun ExternalImportResultRoute(
    openSettings: () -> Unit,
    openImport: () -> Unit,
) {
    ExternalImportResultScreen(
        openSettings = openSettings,
        openImport = openImport,
    )
}
