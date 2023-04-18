package com.twofasapp.browserextension.ui.pairing.progress

data class PairingProgressUiState(
    val isPairing: Boolean = true,
    val isPairingSuccess: Boolean = false,
    val code: Int? = null
)