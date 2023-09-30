package com.twofasapp.feature.browserext.ui.pairing

internal data class BrowserExtPairingUiState(
    val pairing: Boolean = true,
    val pairingResult: PairingResult = PairingResult.Success,
)

internal sealed interface PairingResult {
    data object Success : PairingResult
    data object Failure : PairingResult
    data object AlreadyPaired : PairingResult
}