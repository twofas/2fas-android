package com.twofasapp.feature.externalimport.navigation

import androidx.compose.runtime.Composable
import com.twofasapp.feature.externalimport.domain.ImportType
import com.twofasapp.feature.externalimport.ui.main.ExternalImportScreen
import com.twofasapp.feature.externalimport.ui.result.ExternalImportResultScreen
import com.twofasapp.feature.externalimport.ui.scan.ExternalImportScanScreen
import com.twofasapp.feature.externalimport.ui.selector.ExternalImportSelectorScreen

@Composable
fun ExternalImportSelectorRoute() {
    ExternalImportSelectorScreen()
}

@Composable
fun ExternalImportRoute(
    importType: ImportType,
) {
    ExternalImportScreen(importType = importType)
}

@Composable
fun ExternalImportScanRoute(
    importType: ImportType,
) {
    ExternalImportScanScreen(importType = importType)
}

@Composable
fun ExternalImportResultRoute(
    importType: ImportType,
    importFileUri: String?,
    importFileContent: String?,
) {
    ExternalImportResultScreen(
        importType = importType,
        importFileUri = importFileUri,
        importFileContent = importFileContent,
    )
}