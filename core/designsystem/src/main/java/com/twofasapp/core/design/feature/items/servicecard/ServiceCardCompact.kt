package com.twofasapp.core.design.feature.items.servicecard

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.PreviewCards
import com.twofasapp.core.design.feature.items.ServiceState
import com.twofasapp.core.design.feature.items.ServiceStyle
import com.twofasapp.core.design.feature.items.servicecard.base.CodePlacement
import com.twofasapp.core.design.feature.items.servicecard.base.ServiceCardBase

@Composable
internal fun ServiceCardCompact(
    state: ServiceState,
    showNextCode: Boolean,
    hideCodes: Boolean,
    containerColor: Color,
    onClick: (() -> Unit)?,
    onLongClick: (() -> Unit)?,
    onIncrementCounterClick: (() -> Unit)?,
    onRevealClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    ServiceCardBase(
        state = state,
        showNextCode = showNextCode,
        hideCodes = hideCodes,
        containerColor = containerColor,
        onClick = onClick,
        onLongClick = onLongClick,
        onIncrementCounterClick = onIncrementCounterClick,
        onRevealClick = onRevealClick,
        modifier = modifier,
        codePlacement = CodePlacement.End,
        minHeight = 64.dp,
        imageSize = 28.dp,
        nameTextStyle = MdtTheme.typo.xs.medium,
        nameSpacing = 2.dp,
        infoTextStyle = MdtTheme.typo.xs2.normal,
        codeTextStyle = (if (state.code.length > 6) MdtTheme.typo.lg else MdtTheme.typo.xl).light,
        codeSpacing = 4.dp,
        codeWithNextCodeTextStyle = (if (state.code.length > 6) MdtTheme.typo.base else MdtTheme.typo.lg).light,
        nextCodeTextStyle = MdtTheme.typo.xs2.normal,
        nextCodeEmphasizedTextStyle = MdtTheme.typo.xs.normal,
        nextCodePadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
        timerSize = 28.dp,
        timerStrokeWidth = 2.dp,
        timerTextStyle = MdtTheme.typo.xs4.medium,
        actionButtonSize = 28.dp,
        actionButtonIconSize = 14.dp,
    )
}

@PreviewLightDark
@Composable
private fun Preview() {
    PreviewCards(style = ServiceStyle.Compact)
}