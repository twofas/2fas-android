package com.twofasapp.browserextension.ui.pairing.progress

import androidx.compose.runtime.Composable

class PairingProgressScreenFactory {

    @Composable
    fun create(extensionId: String) {
        PairingProgressScreen(extensionId)
    }
}