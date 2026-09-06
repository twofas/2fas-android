package com.twofasapp.core.design.feature.items.servicecard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.PreviewCards
import com.twofasapp.core.design.feature.items.ServiceAuthType
import com.twofasapp.core.design.feature.items.ServiceState
import com.twofasapp.core.design.feature.items.ServiceStyle
import com.twofasapp.core.design.foundation.other.Space

@Composable
internal fun ServiceCardDefault(
    state: ServiceState,
    showNextCode: Boolean,
    hideCodes: Boolean,
    containerColor: Color,
    onClick: (() -> Unit)?,
    onLongClick: (() -> Unit)?,
    onIncrementCounterClick: (() -> Unit)?,
    onRevealClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    minHeight: Dp = 120.dp,
    imageSize: Dp = 36.dp,
    nameTextStyle: TextStyle = MdtTheme.typo.sm.medium,
    nameSpacing: Dp = 4.dp,
    infoTextStyle: TextStyle = MdtTheme.typo.xs.normal,
    codeTextStyle: TextStyle = (if (state.code.length > 6) MdtTheme.typo.xl4 else MdtTheme.typo.xl5).light,
    codeWithNextCodeTextStyle: TextStyle = (if (state.code.length > 6) MdtTheme.typo.xl3 else MdtTheme.typo.xl4).light,
    codeSpacing: Dp = 10.dp,
    nextCodeTextStyle: TextStyle = MdtTheme.typo.xs.normal,
    nextCodeEmphasizedTextStyle: TextStyle = (if (state.code.length > 6) MdtTheme.typo.sm else MdtTheme.typo.base).normal,
    nextCodePadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 5.dp),
    timerSize: Dp = 36.dp,
    timerTextStyle: TextStyle = MdtTheme.typo.xs2.medium,
    actionButtonSize: Dp = 40.dp,
    actionButtonIconSize: Dp = 20.dp,
) {
    val revealed = state.revealed || hideCodes.not()

    ServiceCardContainer(
        badgeColor = state.badgeColor,
        containerColor = containerColor,
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = modifier
            .heightIn(min = minHeight)
            .height(IntrinsicSize.Min),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ServiceCardImage(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .size(imageSize),
                type = state.imageType,
                iconLight = state.iconLight,
                iconDark = state.iconDark,
                labelText = state.labelText,
                labelColor = state.labelColor,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
            ) {
                Space(4.dp)

                Text(
                    text = state.name,
                    style = nameTextStyle,
                    color = MdtTheme.color.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                )

                if (state.info.isNullOrEmpty().not()) {
                    Space(nameSpacing)

                    Text(
                        text = state.info,
                        style = infoTextStyle,
                        color = MdtTheme.color.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                    )
                }

                Space(codeSpacing)

                ServiceCardCode(
                    state = state,
                    revealed = revealed,
                    showNextCode = showNextCode,
                    codeTextStyle = codeTextStyle,
                    codeWithNextCodeTextStyle = codeWithNextCodeTextStyle,
                    nextCodeTextStyle = nextCodeTextStyle,
                    nextCodeEmphasizedTextStyle = nextCodeEmphasizedTextStyle,
                    nextCodePadding = nextCodePadding,
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
        ) {
            if (revealed) {
                when (state.authType) {
                    ServiceAuthType.Totp,
                    ServiceAuthType.Steam,
                    -> {
                        ServiceTimerRing(
                            timer = state.timer,
                            progress = state.progress,
                            size = timerSize,
                            textStyle = timerTextStyle,
                        )
                    }

                    ServiceAuthType.Hotp -> {
                        ActionButton(
                            icon = MdtIcons.Refresh,
                            size = actionButtonSize,
                            iconSize = actionButtonIconSize,
                            onClick = onIncrementCounterClick,
                            enabled = state.hotpCounterEnabled,
                        )
                    }
                }
            } else {
                ActionButton(
                    icon = MdtIcons.Visibility,
                    size = actionButtonSize,
                    iconSize = actionButtonIconSize,
                    onClick = onRevealClick,
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: Painter,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    iconSize: Dp = 20.dp,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MdtTheme.color.surfaceContainerHighest)
            .clickable(enabled = onClick != null && enabled) { onClick?.invoke() }
            .alpha(if (enabled) 1f else 0.3f),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = MdtTheme.color.onSurfaceVariant,
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    PreviewCards(style = ServiceStyle.Default)
}