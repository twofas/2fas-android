package com.twofasapp.browserextension.ui.browser

import androidx.compose.runtime.Composable
import com.twofasapp.browserextension.ui.pairing.scan.PairingScanScreen

class BrowserDetailsScreenFactory {

    @Composable
    fun create(extensionId: String) {
        BrowserDetailsScreen(extensionId)
    }
}