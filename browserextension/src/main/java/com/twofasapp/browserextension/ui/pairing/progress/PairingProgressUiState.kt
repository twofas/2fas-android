package com.twofasapp.browserextension.ui.pairing.progress

internal data class PairingProgressUiState(
    val isPairing: Boolean = true,
    val isPairingSuccess: Boolean = false,
    val code: Int? = null
)