package com.twofasapp.designsystem

import androidx.compose.runtime.Composable

object TwsTheme {
    val color: TwsColors
        @Composable
        get() = LocalTwsColors.current

    val typo: TwsTypo
        @Composable
        get() = TwsTypo

    val shape: TwsShape
        @Composable
        get() = TwsShape

    val dimen: TwsDimen
        @Composable
        get() = TwsDimen
}