package com.twofasapp.design.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.luminance
import com.twofasapp.designsystem.TwTheme


@Composable
fun isNight() = TwTheme.color.background.luminance() < 0.5