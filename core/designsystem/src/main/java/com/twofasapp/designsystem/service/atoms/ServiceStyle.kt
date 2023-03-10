package com.twofasapp.designsystem.service.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twofasapp.designsystem.TwTheme

internal data class ServiceTextStyle(
    val nameTextStyle: TextStyle,
    val infoTextStyle: TextStyle,
    val codeTextStyle: TextStyle,
    val imageLabelTextStyle: TextStyle,
    val timerTextStyle: TextStyle,
)

internal data class ServiceDimens(
    val cellHeight: Dp,
    val cellHeightInEdit: Dp,
    val imageSize: Dp,
    val timerSize: Dp,
)

internal object ServiceTextDefaults {
    @Composable
    fun default() = ServiceTextStyle(
        nameTextStyle = TwTheme.typo.body3.copy(fontWeight = FontWeight.Medium),
        infoTextStyle = TwTheme.typo.body4.copy(fontWeight = FontWeight.Normal),
        codeTextStyle = TwTheme.typo.h1.copy(fontWeight = FontWeight.ExtraLight),
        imageLabelTextStyle = TwTheme.typo.body2,
        timerTextStyle = TwTheme.typo.caption,
    )

    @Composable
    fun compact() = ServiceTextStyle(
        nameTextStyle = TwTheme.typo.caption.copy(fontWeight = FontWeight.Medium),
        infoTextStyle = TwTheme.typo.caption.copy(fontWeight = FontWeight.Normal),
        codeTextStyle = TwTheme.typo.h3.copy(fontWeight = FontWeight.Light),
        imageLabelTextStyle = TwTheme.typo.body4,
        timerTextStyle = TwTheme.typo.caption.copy(fontSize = 11.sp),
    )

    @Composable
    fun modal() = ServiceTextStyle(
        nameTextStyle = TwTheme.typo.title.copy(fontWeight = FontWeight.Normal),
        infoTextStyle = TwTheme.typo.body1.copy(fontWeight = FontWeight.Normal),
        codeTextStyle = TwTheme.typo.h1.copy(fontWeight = FontWeight.ExtraLight),
        imageLabelTextStyle = TwTheme.typo.body2,
        timerTextStyle = TwTheme.typo.caption,
    )
}

internal object ServiceDimensDefaults {
    @Composable
    fun default() = ServiceDimens(
        cellHeight = 124.dp,
        cellHeightInEdit = 64.dp,
        imageSize = 36.dp,
        timerSize = 32.dp,
    )

    @Composable
    fun compact() = ServiceDimens(
        cellHeight = 80.dp,
        cellHeightInEdit = 64.dp,
        imageSize = 32.dp,
        timerSize = 28.dp,
    )
}
