package com.twofasapp.core.design.feature.items.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.ktx.dpToSp

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
    val labelPillHeight: Dp,
    val labelPillWidth: Dp,
    val timerSize: Dp,
)

internal object ServiceTextDefaults {
    @Composable
    fun default() = ServiceTextStyle(
        nameTextStyle = MdtTheme.typo.body3.copy(fontWeight = FontWeight.Medium),
        infoTextStyle = MdtTheme.typo.body4.copy(fontWeight = FontWeight.Normal),
        codeTextStyle = MdtTheme.typo.codeExtraLight,
        imageLabelTextStyle = MdtTheme.typo.body3.copy(fontWeight = FontWeight.Bold, fontSize = dpToSp(dp = 14.dp), lineHeight = dpToSp(dp = 20.dp)),
        timerTextStyle = MdtTheme.typo.caption,
    )

    @Composable
    fun compact() = ServiceTextStyle(
        nameTextStyle = MdtTheme.typo.caption.copy(fontWeight = FontWeight.Medium),
        infoTextStyle = MdtTheme.typo.caption.copy(fontWeight = FontWeight.Normal),
        codeTextStyle = MdtTheme.typo.codeLightSmall,
        imageLabelTextStyle = MdtTheme.typo.body3.copy(fontWeight = FontWeight.Bold, fontSize = dpToSp(dp = 11.dp), lineHeight = dpToSp(dp = 15.dp)),
        timerTextStyle = MdtTheme.typo.caption.copy(fontSize = 11.sp),
    )

    @Composable
    fun modal() = ServiceTextStyle(
        nameTextStyle = MdtTheme.typo.title.copy(fontWeight = FontWeight.Normal),
        infoTextStyle = MdtTheme.typo.body1.copy(fontWeight = FontWeight.Normal),
        codeTextStyle = MdtTheme.typo.codeExtraLight,
        imageLabelTextStyle = MdtTheme.typo.body3.copy(fontWeight = FontWeight.Bold, fontSize = dpToSp(dp = 14.dp), lineHeight = dpToSp(dp = 20.dp)),
        timerTextStyle = MdtTheme.typo.caption,
    )
}

internal object ServiceDimensDefaults {
    @Composable
    fun default() = ServiceDimens(
        cellHeight = 130.dp,
        cellHeightInEdit = 64.dp,
        imageSize = 40.dp,
        labelPillHeight = 18.dp,
        labelPillWidth = 28.dp,
        timerSize = 32.dp,
    )

    @Composable
    fun compact() = ServiceDimens(
        cellHeight = 80.dp,
        cellHeightInEdit = 64.dp,
        imageSize = 30.dp,
        labelPillHeight = 13.dp,
        labelPillWidth = 21.dp,
        timerSize = 28.dp,
    )
}