package com.twofasapp.designsystem.service

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwIconButton

@Composable
fun Service(
    state: ServiceState,
    style: ServiceStyle,
    modifier: Modifier = Modifier,
    containerColor: Color = TwTheme.color.background,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    when (style) {
        ServiceStyle.Default -> {
            ServiceDefault(
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

        ServiceStyle.Edit -> {
            ServiceWithoutCode(
                state = state,
                imageSize = 36.dp,
                showBadge = true,
                containerColor = containerColor,
                modifier = modifier,
            ) {
                TwIconButton(TwIcons.DragHandle, enabled = false)
            }
        }

        ServiceStyle.Compact -> {}
    }
}


internal val ServicePreview = ServiceState(
    name = "Service Name",
    info = "Additional Info",
    code = "123456",
    nextCode = "789987",
    timer = 10,
    progress = .33f,
    imageType = ServiceImageType.Label,
    iconLight = "",
    iconDark = "",
    labelText = "2F",
    labelColor = Color.Red,
    badgeColor = Color.Red,
)