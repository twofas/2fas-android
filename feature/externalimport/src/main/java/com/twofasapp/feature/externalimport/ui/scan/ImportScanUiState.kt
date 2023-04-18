package com.twofasapp.feature.externalimport.ui.scan

internal data class ImportScanUiState(
    val isSuccess: Boolean = false,
    val content: String = "",
    val showErrorDialog: Boolean = false,
    val errorDialogTitle: Int = 0,
    val errorDialogMessage: Int = 0,
    val errorDialogAction: () -> Unit = {},
)