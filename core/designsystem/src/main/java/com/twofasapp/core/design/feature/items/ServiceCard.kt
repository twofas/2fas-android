package com.twofasapp.core.design.feature.items

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.servicecard.ServiceCardCompact
import com.twofasapp.core.design.feature.items.servicecard.ServiceCardDefault
import com.twofasapp.core.design.foundation.checked.CheckIcon
import com.twofasapp.core.design.foundation.preview.PreviewTheme

@Composable
fun ServiceCard(
    modifier: Modifier = Modifier,
    state: ServiceState,
    style: ServiceStyle = ServiceStyle.Default,
    editMode: Boolean = false,
    selected: Boolean = false,
    showNextCode: Boolean = false,
    hideCodes: Boolean = false,
    containerColor: Color = MdtTheme.color.surfaceContainer,
    dragHandleVisible: Boolean = true,
    dragModifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onIncrementCounterClick: (() -> Unit)? = null,
    onRevealClick: (() -> Unit)? = null,
) {
    val cardModifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 4.dp)

    when {
        editMode -> {
            ServiceCardSimple(
                state = state,
                containerColor = if (selected) MdtTheme.color.surfaceContainerHighest else containerColor,
                onClick = onClick,
                onLongClick = onLongClick,
                modifier = cardModifier,
            ) {
                CheckIcon(
                    checked = selected,
                    color = MdtTheme.color.primary,
                    size = 20.dp,
                )

                if (dragHandleVisible) {
                    Icon(
                        painter = MdtIcons.DragHandle,
                        tint = MdtTheme.color.iconTint,
                        contentDescription = null,
                        modifier = dragModifier.scale(0.85f),
                    )
                }
            }
        }

        style == ServiceStyle.Default -> {
            ServiceCardDefault(
                state = state,
                showNextCode = showNextCode,
                hideCodes = hideCodes,
                containerColor = containerColor,
                onClick = onClick,
                onLongClick = onLongClick,
                onIncrementCounterClick = onIncrementCounterClick,
                onRevealClick = onRevealClick,
                modifier = cardModifier,
            )
        }

        else -> {
            ServiceCardCompact(
                state = state,
                showNextCode = showNextCode,
                hideCodes = hideCodes,
                containerColor = containerColor,
                onClick = onClick,
                onLongClick = onLongClick,
                onIncrementCounterClick = onIncrementCounterClick,
                onRevealClick = onRevealClick,
                modifier = cardModifier,
            )
        }
    }
}

internal const val ServiceExpireTransitionThreshold = 5

@Composable
fun animateExpireColor(timer: Int): State<Color> {
    return animateColorAsState(
        targetValue = if (timer > ServiceExpireTransitionThreshold) {
            MdtTheme.color.onSurface
        } else {
            MdtTheme.color.primary
        },
        animationSpec = TweenSpec(),
        label = "ServiceExpireTransition",
    )
}

enum class ServiceStyle {
    Default, Compact
}

enum class ServiceAuthType {
    Totp, Hotp, Steam
}

enum class ServiceImageType {
    Icon, Label
}

fun String.formatCode(): String {
    if (isEmpty()) return ""

    return when (this.length) {
        5 -> if (this.toIntOrNull() == null) take(5) else "${take(3)} ${takeLast(2)}"
        6 -> "${take(3)} ${takeLast(3)}"
        7 -> "${take(4)} ${takeLast(3)}"
        8 -> "${take(4)} ${takeLast(4)}"
        else -> this
    }
}

@Composable
internal fun PreviewCards(
    style: ServiceStyle = ServiceStyle.Default,
    editMode: Boolean = false,
) {
    PreviewTheme {
        Column(modifier = Modifier.fillMaxWidth()) {
            listOf(
                ServicePreview.copy(timer = 3, progress = 0.13f),
                ServicePreview.copy(timer = 21, progress = 0.7f, badgeColor = Color.Unspecified),
                ServicePreview.copy(timer = 21, progress = 0.7f, badgeColor = Color.Unspecified, info = null),
                ServicePreview.copy(timer = 3, revealed = false, badgeColor = Color.Green),
                ServicePreview.copy(authType = ServiceAuthType.Hotp, badgeColor = Color.Blue),
            ).forEachIndexed { index, state ->
                ServiceCard(
                    state = state,
                    style = style,
                    editMode = editMode,
                    selected = index % 2 == 0,
                    showNextCode = true,
                    hideCodes = true,
                    onClick = {},
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewDefault() {
    PreviewCards(style = ServiceStyle.Default)
}

@PreviewLightDark
@Composable
private fun PreviewCompact() {
    PreviewCards(style = ServiceStyle.Compact)
}

@PreviewLightDark
@Composable
private fun PreviewEdit() {
    PreviewCards(editMode = true)
}

internal val ServicePreview = ServiceState(
    name = "Service Name",
    info = "Additional Info",
    code = "123456",
    nextCode = "789987",
    timer = 10,
    hotpCounter = 1,
    progress = .33f,
    imageType = ServiceImageType.Label,
    authType = ServiceAuthType.Totp,
    iconLight = "",
    iconDark = "",
    labelText = "2F",
    labelColor = Color.Red,
    badgeColor = Color.Red,
    revealed = true,
)