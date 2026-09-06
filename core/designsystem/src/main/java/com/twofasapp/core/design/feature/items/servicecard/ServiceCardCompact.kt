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
    ServiceCardDefault(
        state = state,
        showNextCode = showNextCode,
        hideCodes = hideCodes,
        containerColor = containerColor,
        onClick = onClick,
        onLongClick = onLongClick,
        onIncrementCounterClick = onIncrementCounterClick,
        onRevealClick = onRevealClick,
        modifier = modifier,
        minHeight = 86.dp,
        imageSize = 32.dp,
        nameTextStyle = MdtTheme.typo.xs.medium,
        nameSpacing = 0.dp,
        infoTextStyle = MdtTheme.typo.xs2.normal,
        codeTextStyle = (if (state.code.length > 6) MdtTheme.typo.xl2 else MdtTheme.typo.xl3).light,
        codeSpacing = 6.dp,
        codeWithNextCodeTextStyle = (if (state.code.length > 6) MdtTheme.typo.xl else MdtTheme.typo.xl2).light,
        nextCodeTextStyle = MdtTheme.typo.xs2.normal,
        nextCodeEmphasizedTextStyle = (if (state.code.length > 6) MdtTheme.typo.xs else MdtTheme.typo.sm).normal,
        nextCodePadding = PaddingValues(horizontal = 6.dp, vertical = 3.dp),
        timerSize = 32.dp,
        timerTextStyle = MdtTheme.typo.xs3.medium,
        actionButtonSize = 32.dp,
        actionButtonIconSize = 16.dp,
    )
}

@PreviewLightDark
@Composable
private fun Preview() {
    PreviewCards(style = ServiceStyle.Compact)
}