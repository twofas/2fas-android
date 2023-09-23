package com.twofasapp.feature.browserext.navigation

import androidx.compose.runtime.Composable
import com.twofasapp.feature.browserext.ui.details.BrowserExtDetailsScreen
import com.twofasapp.feature.browserext.ui.main.BrowserExtScreen
import com.twofasapp.feature.browserext.ui.pairing.BrowserExtPairingScreen
import com.twofasapp.feature.browserext.ui.permission.BrowserExtPermissionScreen
import com.twofasapp.feature.browserext.ui.scan.BrowserExtScanScreen

@Composable
fun BrowserExtRoute(
    openScan: () -> Unit = {},
    openDetails: (String) -> Unit = {},
) {
    BrowserExtScreen(
        openScan = openScan,
        openDetails = openDetails,
    )
}

@Composable
fun BrowserExtPermissionRoute(
    openMain: () -> Unit,
) {
    BrowserExtPermissionScreen(
        openMain = openMain,
    )
}

@Composable
fun BrowserExtScanRoute(
    openProgress: (String) -> Unit,
) {
    BrowserExtScanScreen(
        openProgress = openProgress,
    )
}

@Composable
fun BrowserExtPairingRoute(
    openMain: () -> Unit,
    openPermission: () -> Unit,
    openScan: () -> Unit,
) {
    BrowserExtPairingScreen(
        openMain = openMain,
        openPermission = openPermission,
        openScan = openScan,
    )
}

@Composable
fun BrowserExtDetailsRoute(
    openMain: () -> Unit,
) {
    BrowserExtDetailsScreen(
        openMain = openMain,
    )
}
