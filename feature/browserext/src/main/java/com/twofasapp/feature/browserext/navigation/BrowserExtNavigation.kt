package com.twofasapp.feature.browserext.navigation

import androidx.compose.runtime.Composable
import com.twofasapp.feature.browserext.ui.details.BrowserExtDetailsScreen
import com.twofasapp.feature.browserext.ui.main.BrowserExtScreen
import com.twofasapp.feature.browserext.ui.pairing.BrowserExtPairingScreen
import com.twofasapp.feature.browserext.ui.permission.BrowserExtPermissionScreen
import com.twofasapp.feature.browserext.ui.scan.BrowserExtScanScreen

@Composable
fun BrowserExtRoute() {
    BrowserExtScreen()
}

@Composable
fun BrowserExtPermissionRoute() {
    BrowserExtPermissionScreen()
}

@Composable
fun BrowserExtScanRoute() {
    BrowserExtScanScreen()
}

@Composable
fun BrowserExtPairingRoute(
    extensionId: String,
) {
    BrowserExtPairingScreen(extensionId = extensionId)
}

@Composable
fun BrowserExtDetailsRoute(
    extensionId: String,
) {
    BrowserExtDetailsScreen(extensionId = extensionId)
}