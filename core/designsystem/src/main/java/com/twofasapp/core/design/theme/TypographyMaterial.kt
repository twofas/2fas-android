package com.twofasapp.core.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

@Immutable
@Stable
class TypographyMaterial(
    private val colorTokens: ColorTokens,
) {
    val displayLarge: TextStyle
        @Composable
        get() = MaterialTheme.typography.displayLarge.copy(colorTokens.onBackground)

    val displayMedium: TextStyle
        @Composable
        get() = MaterialTheme.typography.displayMedium.copy(colorTokens.onBackground)

    val displaySmall: TextStyle
        @Composable
        get() = MaterialTheme.typography.displaySmall.copy(colorTokens.onBackground)

    val headlineLarge: TextStyle
        @Composable
        get() = MaterialTheme.typography.headlineLarge.copy(colorTokens.onBackground)

    val headlineMedium: TextStyle
        @Composable
        get() = MaterialTheme.typography.headlineMedium.copy(colorTokens.onBackground)

    val headlineSmall: TextStyle
        @Composable
        get() = MaterialTheme.typography.headlineSmall.copy(colorTokens.onBackground)

    val titleLarge: TextStyle
        @Composable
        get() = MaterialTheme.typography.titleLarge.copy(colorTokens.onBackground)

    val titleMedium: TextStyle
        @Composable
        get() = MaterialTheme.typography.titleMedium.copy(colorTokens.onBackground)

    val titleSmall: TextStyle
        @Composable
        get() = MaterialTheme.typography.titleSmall.copy(colorTokens.onBackground)

    val bodyLarge: TextStyle
        @Composable
        get() = MaterialTheme.typography.bodyLarge.copy(colorTokens.onBackground)

    val bodyMedium: TextStyle
        @Composable
        get() = MaterialTheme.typography.bodyMedium.copy(colorTokens.onBackground)

    val bodySmall: TextStyle
        @Composable
        get() = MaterialTheme.typography.bodySmall.copy(colorTokens.onBackground)

    val labelLarge: TextStyle
        @Composable
        get() = MaterialTheme.typography.labelLarge.copy(colorTokens.onBackground)

    val labelLargeProminent: TextStyle
        @Composable
        get() = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold, color = colorTokens.onBackground)

    val labelMedium: TextStyle
        @Composable
        get() = MaterialTheme.typography.labelMedium.copy(colorTokens.onBackground)

    val labelMediumProminent: TextStyle
        @Composable
        get() = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, color = colorTokens.onBackground)

    val labelSmall: TextStyle
        @Composable
        get() = MaterialTheme.typography.labelSmall.copy(color = colorTokens.onBackground)
}