package com.twofasapp.core.design.feature.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.atoms.ServiceCode
import com.twofasapp.core.design.feature.items.atoms.ServiceHotp
import com.twofasapp.core.design.feature.items.atoms.ServiceImage
import com.twofasapp.core.design.feature.items.atoms.ServiceInfo
import com.twofasapp.core.design.feature.items.atoms.ServiceName
import com.twofasapp.core.design.feature.items.atoms.ServiceTextDefaults
import com.twofasapp.core.design.feature.items.atoms.ServiceTimer
import com.twofasapp.core.design.feature.items.atoms.formatCode

@Composable
fun DsServiceModal(
    state: ServiceState,
    showNextCode: Boolean = false,
    hideCodes: Boolean = false,
    modifier: Modifier = Modifier,
    containerColor: Color = MdtTheme.color.background,
    onIncrementCounterClick: (() -> Unit)? = null,
    onRevealClick: (() -> Unit)? = null,
) {
    val textStyles = ServiceTextDefaults.modal()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(164.dp)
            .background(containerColor)
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        ServiceName(
            text = state.name,
            textStyles = ServiceTextDefaults.modal(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )

        ServiceInfo(
            text = state.info,
            textStyles = ServiceTextDefaults.modal(),
            style = ServiceStyle.Default,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ServiceImage(
                type = state.imageType,
                iconLight = state.iconLight,
                iconDark = state.iconDark,
                labelText = state.labelText,
                labelColor = state.labelColor,
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(IntrinsicSize.Max),
            ) {
                ServiceCode(
                    code = state.code,
                    nextCode = state.nextCode,
                    timer = state.timer,
                    nextCodeVisible = state.isNextCodeEnabled(showNextCode),
                    animateColor = state.authType == ServiceAuthType.Totp || state.authType == ServiceAuthType.Steam,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (state.revealed || hideCodes.not()) 1f else 0f),
                    textStyles = textStyles,
                )

                if (state.revealed.not() && hideCodes) {
                    HiddenDots(
                        formattedCode = state.code.formatCode(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                    )
                }
            }

            if (state.revealed || hideCodes.not() || state.authType == ServiceAuthType.Hotp) {
                when (state.authType) {
                    ServiceAuthType.Steam,
                    ServiceAuthType.Totp,
                    -> {
                        ServiceTimer(
                            timer = state.timer,
                            progress = state.progress,
                            textStyles = textStyles,
                            modifier = Modifier.padding(end = 12.dp),
                        )
                    }

                    ServiceAuthType.Hotp -> {
                        ServiceHotp(
                            enabled = state.hotpCounterEnabled,
                            onClick = { onIncrementCounterClick?.invoke() },
                        )
                    }
                }
            } else {
                if (state.authType == ServiceAuthType.Totp || state.authType == ServiceAuthType.Steam) {
                    Box(
                        Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .clickable { onRevealClick?.invoke() },
                    ) {
                        Icon(
                            painter = MdtIcons.Eye,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center),
                            tint = MdtTheme.color.iconTint,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DsServiceModal(state = ServicePreview.copy(revealed = true), hideCodes = true)
        DsServiceModal(state = ServicePreview.copy(revealed = false), hideCodes = true)

        DsServiceModal(state = ServicePreview.copy(revealed = true, authType = ServiceAuthType.Hotp), hideCodes = true)
        DsServiceModal(state = ServicePreview.copy(revealed = false, authType = ServiceAuthType.Hotp), hideCodes = true)
    }
}