package com.twofasapp.design.theme

import androidx.compose.material.Colors
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.twofasapp.designsystem.TwTheme

fun Color.Companion.parse(hexString: String): Color =
    Color(color = android.graphics.Color.parseColor(hexString))

@Composable
fun switchColors(): SwitchColors {
    return SwitchDefaults.colors(
        checkedThumbColor = TwTheme.color.primary,
        checkedTrackColor = TwTheme.color.primary,
        uncheckedThumbColor = Color(0xFFECECEC),
        uncheckedTrackColor = Color(0xFF585858),
    )
}

@Composable
fun radioColors(): RadioButtonColors {
    return RadioButtonDefaults.colors(
        selectedColor = TwTheme.color.primary,
        unselectedColor = Color(0xFF585858),
    )
}

internal val primary = Color(0xFFED1C24)

internal val statusbarLight = Color(0xFFF2F2F2)
internal val statusbarDark = Color(0xFF14151A)

internal val backgroundSecondaryLight = Color(0xFFF5F5F5)
internal val backgroundSecondaryDark = Color(0xFF1F2025)

internal val toolbarLight = Color(0xFFF9F9F9)
internal val toolbarDark = Color(0xFF15161B)

internal val textPrimaryLight = Color(0xFF000000)
internal val textPrimaryInverseLight = Color(0xFFFFFFFF)

internal val textPrimaryDark = Color(0xFFFFFFFF)
internal val textPrimaryInverseDark = Color(0xFF000000)

internal val textSecondaryLight = Color(0x66000000)
internal val textSecondaryInverseLight = Color(0xB2FFFFFF)

internal val textSecondaryDark = Color(0x8AFFFFFF)
internal val textSecondaryInverseDark = Color(0xFF000000)

internal val iconLight = Color(0x61000000)
internal val iconDark = Color(0x80FFFFFF)

internal val dividerLight = Color(0x14000000)
internal val dividerDark = Color(0x0FFFFFFF)

internal val dialogLight = Color(0xFFFFFFFF)
internal val dialogDark = Color(0xFF454545)

internal val textFieldOutlineLight = Color(0x4D000000)
internal val textFieldOutlineDark = Color(0x66FFFFFF)

internal val textFieldHintLight = Color(0x61000000)
internal val textFieldHintDark = Color(0x80FFFFFF)

val Colors.toolbar: Color
    get() = if (isLight) toolbarLight else toolbarDark

val Colors.backgroundSecondary: Color
    get() = if (isLight) backgroundSecondaryLight else backgroundSecondaryDark

val Colors.toolbarContent: Color
    get() = if (isLight) textPrimaryLight else textPrimaryDark

val Colors.textPrimary: Color
    get() = if (isLight) textPrimaryLight else textPrimaryDark

val Colors.textPrimaryInverse: Color
    get() = if (isLight) textPrimaryInverseLight else textPrimaryInverseDark

val Colors.textSecondary: Color
    get() = if (isLight) textSecondaryLight else textSecondaryDark

val Colors.textSecondaryInverse: Color
    get() = if (isLight) textSecondaryInverseLight else textSecondaryInverseDark

val Colors.icon: Color
    get() = if (isLight) iconLight else iconDark

val Colors.divider: Color
    get() = if (isLight) dividerLight else dividerDark

val Colors.dialog: Color
    get() = if (isLight) dialogLight else dialogDark

val Colors.textFieldOutline: Color
    get() = if (isLight) textFieldOutlineLight else textFieldOutlineDark

val Colors.textFieldHint: Color
    get() = if (isLight) textFieldHintLight else textFieldHintDark

val Colors.textFieldDisabledText: Color
    get() = if (isLight) textPrimary else textPrimaryDark