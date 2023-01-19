package com.twofasapp.feature.home.navigation

import androidx.compose.ui.graphics.painter.Painter

internal data class BottomNavItem(
    val title: String,
    val icon: Painter,
    val route: String,
)