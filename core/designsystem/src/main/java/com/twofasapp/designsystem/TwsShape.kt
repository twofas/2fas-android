package com.twofasapp.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Immutable
@Stable
object TwsShape {
    val rect = RectangleShape
    val roundedButton = RoundedCornerShape(24.dp)
    val roundedDefault = RoundedCornerShape(12.dp)
}