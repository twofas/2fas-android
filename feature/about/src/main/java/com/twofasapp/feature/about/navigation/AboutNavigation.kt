package com.twofasapp.feature.about.navigation

import androidx.compose.runtime.Composable
import com.twofasapp.feature.about.ui.about.AboutScreen
import com.twofasapp.feature.about.ui.licenses.AboutLicensesScreen

@Composable
fun AboutRoute(
    openLicenses: () -> Unit,
) {
    AboutScreen(
        openLicenses = openLicenses,
    )
}

@Composable
fun AboutLicensesRoute() {
    AboutLicensesScreen()
}