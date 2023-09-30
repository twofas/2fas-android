package com.twofasapp.feature.trash.navigation

import androidx.compose.runtime.Composable
import com.twofasapp.feature.trash.ui.dispose.DisposeScreen
import com.twofasapp.feature.trash.ui.trash.TrashScreen

@Composable
fun TrashRoute(
    openDispose: (Long) -> Unit
) {
    TrashScreen(openDispose = openDispose)
}

@Composable
fun DisposeRoute(
    navigateBack: () -> Unit
) {
    DisposeScreen(navigateBack = navigateBack)
}