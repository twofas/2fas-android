package com.twofasapp.browserextension.ui.pairing.scan

data class PairingScanUiState(
    val isSuccess: Boolean = false,
    val extensionId: String = "",
    val showErrorDialog: Boolean = false,
    val errorDialogTitle: Int = 0,
    val errorDialogMessage: Int = 0,
    val errorDialogAction: () -> Unit = {},
)