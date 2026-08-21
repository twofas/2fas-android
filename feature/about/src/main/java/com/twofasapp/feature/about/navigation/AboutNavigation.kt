package com.twofasapp.feature.about.navigation

import androidx.compose.runtime.Composable
import com.twofasapp.feature.about.ui.about.AboutScreen
import com.twofasapp.feature.about.ui.licenses.AboutLicensesScreen

@Composable
fun AboutRoute() {
    AboutScreen()
}

@Composable
fun AboutLicensesRoute() {
    AboutLicensesScreen()
}