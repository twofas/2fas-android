package com.twofasapp.design.theme

import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.twofasapp.designsystem.TwTheme

fun Color.Companion.parse(hexString: String): Color =
    Color(color = android.graphics.Color.parseColor(hexString))

@Composable
fun radioColors(): RadioButtonColors {
    return RadioButtonDefaults.colors(
        selectedColor = TwTheme.color.primary,
        unselectedColor = Color(0xFF585858),
    )
}