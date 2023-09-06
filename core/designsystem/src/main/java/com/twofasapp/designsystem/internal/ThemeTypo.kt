package com.twofasapp.designsystem.internal

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.twofasapp.designsystem.R
import com.twofasapp.designsystem.TwTheme

@Immutable
@Stable
class ThemeTypo {

    val h1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 42.sp,
        lineHeight = 42.sp,
    )

    val h2 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    )

    val h3 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    )

    val title = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 26.sp,
    )

    val body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    )

    val body2 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    )

    val body3 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 17.sp,
    )

    val body4 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 17.sp,
    )

    val caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    )

    val subhead = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    )

    val codeExtraLight = TextStyle(
        fontFamily = FontFamily(
            Font(resId = R.font.roboto_extra_light)
        ),
        fontSize = 44.sp,
        lineHeight = 44.sp,
    )

    val codeLightSmall = TextStyle(
        fontFamily = FontFamily(
            Font(resId = R.font.roboto_light)
        ),
        fontSize = 24.sp,
        lineHeight = 32.sp,
    )

    val codeLight = TextStyle(
        fontFamily = FontFamily(
            Font(resId = R.font.roboto_light)
        ),
        fontSize = 44.sp,
        lineHeight = 44.sp,
    )

    val codeThin = TextStyle(
        fontFamily = FontFamily(
            Font(resId = R.font.roboto_thin)
        ),
        fontSize = 44.sp,
        lineHeight = 44.sp,
    )
}