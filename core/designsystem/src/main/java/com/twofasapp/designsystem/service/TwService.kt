package com.twofasapp.designsystem.service

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwIconButton
import com.twofasapp.designsystem.service.component.NextCodeGravity
import com.twofasapp.designsystem.service.component.ServiceBadge
import com.twofasapp.designsystem.service.component.ServiceCode
import com.twofasapp.designsystem.service.component.ServiceImage
import com.twofasapp.designsystem.service.component.ServiceInfo
import com.twofasapp.designsystem.service.component.ServiceName
import com.twofasapp.designsystem.service.component.ServiceTimer

data class ServiceStyle(
    val height: Dp,
    val editModeHeight: Dp,
    val imageScale: Float,
    val timerScale: Float,
    val name: TextStyle,
    val info: TextStyle,
    val code: TextStyle,
    val nextCodeGravity: NextCodeGravity,
    val label: TextStyle,
)

object TwServiceDefaults {
    @Composable
    fun defaultStyle() = ServiceStyle(
        height = 128.dp,
        editModeHeight = 64.dp,
        imageScale = 1f,
        timerScale = 1f,
        name = TwTheme.typo.body3.copy(fontWeight = FontWeight.Medium),
        info = TwTheme.typo.body4.copy(fontWeight = FontWeight.Normal),
        code = TwTheme.typo.h1.copy(fontWeight = FontWeight.ExtraLight),
        nextCodeGravity = NextCodeGravity.Below,
        label = TwTheme.typo.body3.copy(fontWeight = FontWeight.Medium),
    )

    @Composable
    fun compactStyle() = ServiceStyle(
        height = 80.dp,
        editModeHeight = 64.dp,
        imageScale = 0.85f,
        timerScale = 0.9f,
        name = TwTheme.typo.caption.copy(fontWeight = FontWeight.Medium),
        info = TwTheme.typo.caption.copy(fontWeight = FontWeight.Normal),
        code = TwTheme.typo.h3.copy(fontWeight = FontWeight.Light),
        nextCodeGravity = NextCodeGravity.End,
        label = TwTheme.typo.body3.copy(fontWeight = FontWeight.Medium),
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TwService(
    state: ServiceState,
    modifier: Modifier = Modifier,
    style: ServiceStyle = TwServiceDefaults.defaultStyle(),
    editMode: Boolean = false,
    showNextCode: Boolean = false,
    dragModifier: Modifier = Modifier,
    containerColor: Color = TwTheme.color.background,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    val codeColor by animateColorAsState(
        targetValue = if (state.timer > 5) TwTheme.color.onSurfacePrimary else TwTheme.color.primary,
        animationSpec = TweenSpec(),
        label = ""
    )

    Column(modifier) {
        Divider(color = TwTheme.color.divider)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (editMode) style.editModeHeight else style.height)
                .background(containerColor)
                .combinedClickable(
                    enabled = onClick != null,
                    onClick = { onClick?.invoke() },
                    onLongClick = { onLongClick?.invoke() },
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            ServiceBadge(
                color = state.badgeColor,
            )

            ServiceImage(
                type = state.imageType,
                iconLight = state.iconLight,
                iconDark = state.iconDark,
                labelText = state.labelText,
                labelColor = state.labelColor,
                modifier = Modifier.scale(style.imageScale)
            )

            Column(modifier = Modifier.weight(1f)) {
                ServiceName(state.name, style = style.name)
                ServiceInfo(state.info, style = style.info)
                if (editMode.not()) {
                    ServiceCode(
                        code = state.code,
                        nextCode = state.nextCode,
                        nextCodeVisible = state.timer <= 5 && showNextCode,
                        nextCodeGravity = style.nextCodeGravity,
                        style = style.code,
                        color = codeColor,
                    )
                }
            }

            if (editMode) {
                TwIconButton(
                    painter = TwIcons.DragHandle,
                    enabled = false,
                    modifier = dragModifier,
                )
            } else {
                ServiceTimer(
                    timer = state.timer,
                    progress = state.progress,
                    color = codeColor,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .scale(style.timerScale),
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDefault() {
    TwService(state = ServicePreview)
}

@Preview
@Composable
private fun PreviewEdit() {
    TwService(state = ServicePreview, editMode = true)
}

@Preview
@Composable
private fun PreviewCompact() {
    TwService(state = ServicePreview, style = TwServiceDefaults.compactStyle())
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