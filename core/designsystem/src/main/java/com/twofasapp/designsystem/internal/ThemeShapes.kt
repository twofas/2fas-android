package com.twofasapp.designsystem.internal

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Immutable
@Stable
class ThemeShapes {
    val rect = RectangleShape
    val circle = CircleShape
    val roundedButton = RoundedCornerShape(24.dp)
    val roundedDefault = RoundedCornerShape(12.dp)
}