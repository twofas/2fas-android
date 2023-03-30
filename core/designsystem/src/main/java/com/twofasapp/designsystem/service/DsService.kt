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
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwIconButton
import com.twofasapp.designsystem.service.atoms.NextCodeGravity
import com.twofasapp.designsystem.service.atoms.ServiceBadge
import com.twofasapp.designsystem.service.atoms.ServiceCode
import com.twofasapp.designsystem.service.atoms.ServiceDimens
import com.twofasapp.designsystem.service.atoms.ServiceDimensDefaults
import com.twofasapp.designsystem.service.atoms.ServiceHotp
import com.twofasapp.designsystem.service.atoms.ServiceImage
import com.twofasapp.designsystem.service.atoms.ServiceInfo
import com.twofasapp.designsystem.service.atoms.ServiceName
import com.twofasapp.designsystem.service.atoms.ServiceTextDefaults
import com.twofasapp.designsystem.service.atoms.ServiceTextStyle
import com.twofasapp.designsystem.service.atoms.ServiceTimer

internal const val ServiceExpireTransitionThreshold = 5

@Composable
fun animateExpireColor(timer: Int): State<Color> {
    return animateColorAsState(
        targetValue = if (timer > ServiceExpireTransitionThreshold) {
            TwTheme.color.onSurfacePrimary
        } else {
            TwTheme.color.primary
        },
        animationSpec = TweenSpec(),
        label = ""
    )
}

enum class ServiceStyle {
    Default, Compact
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DsService(
    state: ServiceState,
    modifier: Modifier = Modifier,
    style: ServiceStyle = ServiceStyle.Default,
    editMode: Boolean = false,
    showNextCode: Boolean = false,
    showExpand: Boolean = true,
    containerColor: Color = TwTheme.color.background,
    dragHandleVisible: Boolean = true,
    dragModifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onIncrementCounterClick: (() -> Unit)? = null,
) {
    val textStyles: ServiceTextStyle = when (style) {
        ServiceStyle.Default -> ServiceTextDefaults.default()
        ServiceStyle.Compact -> ServiceTextDefaults.compact()
    }

    val dimens: ServiceDimens = when (style) {
        ServiceStyle.Default -> ServiceDimensDefaults.default()
        ServiceStyle.Compact -> ServiceDimensDefaults.compact()
    }

    Column(modifier) {
        Divider(color = TwTheme.color.divider)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (editMode) dimens.cellHeightInEdit else dimens.cellHeight)
                .background(containerColor)
                .combinedClickable(
                    enabled = onClick != null,
                    onClick = {
                        if (editMode.not()) {
                            onClick?.invoke()
                        }
                    },
                    onLongClick = {
                        onLongClick?.invoke()
                    },
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
                textStyles = textStyles,
                dimens = dimens,
            )

            Column(modifier = Modifier.weight(1f)) {
                ServiceName(
                    text = state.name,
                    textStyles = textStyles,
                    modifier = Modifier.fillMaxWidth(),
                )

                ServiceInfo(
                    text = state.info,
                    textStyles = textStyles,
                    modifier = Modifier.fillMaxWidth(),
                )

                if (editMode.not()) {
                    ServiceCode(
                        code = state.code,
                        nextCode = state.nextCode,
                        timer = state.timer,
                        nextCodeVisible = state.isNextCodeEnabled(showNextCode),
                        nextCodeGravity = when (style) {
                            ServiceStyle.Default -> NextCodeGravity.Below
                            ServiceStyle.Compact -> NextCodeGravity.End
                        },
                        animateColor = state.authType == ServiceAuthType.Totp,
                        textStyles = textStyles,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            if (editMode.not()) {
                when (state.authType) {
                    ServiceAuthType.Totp -> {
                        ServiceTimer(
                            timer = state.timer,
                            progress = state.progress,
                            textStyles = textStyles,
                            dimens = dimens,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    }

                    ServiceAuthType.Hotp -> {
                        ServiceHotp(
                            enabled = state.hotpCounterEnabled,
                            onClick = { onIncrementCounterClick?.invoke() }
                        )
                    }
                }
            }

            if (editMode && dragHandleVisible) {
                TwIconButton(
                    painter = TwIcons.DragHandle,
                    enabled = false,
                    modifier = dragModifier,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDefault() {
    DsService(state = ServicePreview)
}

@Preview
@Composable
private fun PreviewDefaultHotp() {
    DsService(state = ServicePreview.copy(authType = ServiceAuthType.Hotp))
}


@Preview
@Composable
private fun PreviewCompact() {
    DsService(state = ServicePreview, style = ServiceStyle.Compact)
}

@Preview
@Composable
private fun PreviewEdit() {
    DsService(state = ServicePreview, editMode = true)
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
)