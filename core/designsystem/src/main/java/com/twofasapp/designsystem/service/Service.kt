package com.twofasapp.designsystem.service

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwIconButton

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Service(
    state: ServiceState,
    style: ServiceStyle,
    isInEditMode: Boolean = false,
    containerColor: Color = TwTheme.color.background,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    if (isInEditMode) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(TwTheme.shape.roundedDefault)
                .background(containerColor)
                .combinedClickable(
                    onClick = { onClick() },
                    onLongClick = { onLongClick() },
                )
                .border(1.dp, TwTheme.color.surfaceVariant, TwTheme.shape.roundedDefault)
                .padding(start = 21.dp)
        ) {
            ServiceNoCode(
                name = state.name,
                info = state.info,
                imageType = state.imageType,
                iconLight = state.iconLight,
                iconDark = state.iconDark,
                labelText = state.labelText,
                labelColor = state.labelColor,
                imageSize = 36.dp,
                containerColor = containerColor,
                modifier = Modifier,
            ) {
                TwIconButton(TwIcons.DragHandle, enabled = false)
            }
        }
    } else {
        when (style) {
            ServiceStyle.Normal -> {
                ServiceNormal(
                    state = state,
                    containerColor = containerColor,
                    modifier = modifier,
                    onClick = onClick,
                    onLongClick = onLongClick,
                )
            }

            ServiceStyle.Modal -> {
                ServiceModal(
                    state = state,
                    containerColor = containerColor,
                    modifier = modifier,
                )
            }

            ServiceStyle.Compact -> {}
        }

    }
}