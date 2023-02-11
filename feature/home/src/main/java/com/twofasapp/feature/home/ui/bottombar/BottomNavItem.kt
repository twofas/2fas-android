package com.twofasapp.feature.home.ui.bottombar

import androidx.compose.ui.graphics.painter.Painter

internal data class BottomNavItem(
    val title: String,
    val icon: Painter,
    val iconSelected: Painter,
    val route: String,
)