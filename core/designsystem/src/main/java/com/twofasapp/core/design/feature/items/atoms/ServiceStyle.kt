package com.twofasapp.core.design.feature.items.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.ktx.dpToSp
import com.twofasapp.core.design.theme.CodeExtraLight
import com.twofasapp.core.design.theme.CodeLightSmall

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
        nameTextStyle = MdtTheme.typo.regular.sm.copy(fontWeight = FontWeight.Medium),
        infoTextStyle = MdtTheme.typo.regular.xs.copy(fontWeight = FontWeight.Normal),
        codeTextStyle = CodeExtraLight,
        imageLabelTextStyle = MdtTheme.typo.regular.sm.copy(fontWeight = FontWeight.Bold, fontSize = dpToSp(dp = 14.dp), lineHeight = dpToSp(dp = 20.dp)),
        timerTextStyle = MdtTheme.typo.regular.xs,
    )

    @Composable
    fun compact() = ServiceTextStyle(
        nameTextStyle = MdtTheme.typo.regular.xs.copy(fontWeight = FontWeight.Medium),
        infoTextStyle = MdtTheme.typo.regular.xs.copy(fontWeight = FontWeight.Normal),
        codeTextStyle = CodeLightSmall,
        imageLabelTextStyle = MdtTheme.typo.regular.sm.copy(fontWeight = FontWeight.Bold, fontSize = dpToSp(dp = 11.dp), lineHeight = dpToSp(dp = 15.dp)),
        timerTextStyle = MdtTheme.typo.regular.xs.copy(fontSize = 11.sp),
    )

    @Composable
    fun modal() = ServiceTextStyle(
        nameTextStyle = MdtTheme.typo.regular.xl.copy(fontWeight = FontWeight.Normal),
        infoTextStyle = MdtTheme.typo.regular.base.copy(fontWeight = FontWeight.Normal),
        codeTextStyle = CodeExtraLight,
        imageLabelTextStyle = MdtTheme.typo.regular.sm.copy(fontWeight = FontWeight.Bold, fontSize = dpToSp(dp = 14.dp), lineHeight = dpToSp(dp = 20.dp)),
        timerTextStyle = MdtTheme.typo.regular.xs,
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