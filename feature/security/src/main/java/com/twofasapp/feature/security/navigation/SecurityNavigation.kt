package com.twofasapp.feature.security.navigation

import androidx.compose.runtime.Composable
import com.twofasapp.feature.security.ui.changepin.ChangePinScreen
import com.twofasapp.feature.security.ui.disablepin.DisablePinScreen
import com.twofasapp.feature.security.ui.security.SecurityScreen
import com.twofasapp.feature.security.ui.setuppin.SetupPinScreen

@Composable
fun SecurityRoute() {
    SecurityScreen()
}

@Composable
fun SetupPinRoute() {
    SetupPinScreen()
}

@Composable
fun DisablePinRoute() {
    DisablePinScreen()
}

@Composable
fun ChangePinRoute() {
    ChangePinScreen()
}