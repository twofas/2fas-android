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
import com.twofasapp.core.design.feature.items.servicecard.base.ServiceCardBase

@Composable
internal fun ServiceCardLarge(
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
        minHeight = 120.dp,
        imageSize = 36.dp,
        nameTextStyle = MdtTheme.typo.sm.medium,
        nameSpacing = 4.dp,
        infoTextStyle = MdtTheme.typo.xs.normal,
        codeTextStyle = (if (state.code.length > 6) MdtTheme.typo.xl4 else MdtTheme.typo.xl5).light,
        codeSpacing = 10.dp,
        codeWithNextCodeTextStyle = (if (state.code.length > 6) MdtTheme.typo.xl3 else MdtTheme.typo.xl4).light,
        nextCodeTextStyle = MdtTheme.typo.xs.normal,
        nextCodeEmphasizedTextStyle = (if (state.code.length > 6) MdtTheme.typo.sm else MdtTheme.typo.base).normal,
        nextCodePadding = PaddingValues(horizontal = 8.dp, vertical = 5.dp),
        timerSize = 36.dp,
        timerStrokeWidth = 3.dp,
        timerTextStyle = MdtTheme.typo.xs2.medium,
        actionButtonSize = 40.dp,
        actionButtonIconSize = 20.dp,
    )
}

@PreviewLightDark
@Composable
private fun Preview() {
    PreviewCards(style = ServiceStyle.Large)
}