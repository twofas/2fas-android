package com.twofasapp.core.design.feature.items.servicecard

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.theme.RoundedShape16

@Composable
internal fun ServiceCardContainer(
    modifier: Modifier = Modifier,
    badgeColor: Color,
    containerColor: Color,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedShape16)
            .background(containerColor)
            .drawBehind {
                val badgeWidth = 16.dp.toPx()
                val badgeHeight = size.height * 0.7f
                drawRoundRect(
                    color = badgeColor.takeOrElse { Color.Transparent },
                    topLeft = Offset(x = -12.dp.toPx(), y = (size.height - badgeHeight) / 2),
                    size = Size(width = badgeWidth, height = badgeHeight),
                    cornerRadius = CornerRadius(badgeWidth / 2),
                )
            }
            .combinedClickable(
                enabled = onClick != null,
                onClick = { onClick?.invoke() },
                onLongClick = { onLongClick?.invoke() },
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        content()
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    PreviewTheme {
        ServiceCardContainer(
            badgeColor = MdtTheme.color.primary,
            containerColor = MdtTheme.color.surfaceContainer,
            modifier = Modifier.size(width = 160.dp, height = 64.dp),
            content = {},
        )
    }
}