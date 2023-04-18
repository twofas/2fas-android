package com.twofasapp.designsystem.service

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.service.atoms.ServiceCode
import com.twofasapp.designsystem.service.atoms.ServiceHotp
import com.twofasapp.designsystem.service.atoms.ServiceImage
import com.twofasapp.designsystem.service.atoms.ServiceInfo
import com.twofasapp.designsystem.service.atoms.ServiceName
import com.twofasapp.designsystem.service.atoms.ServiceTextDefaults
import com.twofasapp.designsystem.service.atoms.ServiceTimer

@Composable
fun DsServiceModal(
    state: ServiceState,
    showNextCode: Boolean = false,
    modifier: Modifier = Modifier,
    containerColor: Color = TwTheme.color.background,
    onIncrementCounterClick: (() -> Unit)? = null,
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
                .padding(horizontal = 16.dp)
        )

        ServiceInfo(
            text = state.info,
            textStyles = ServiceTextDefaults.modal(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ServiceImage(
                type = state.imageType,
                iconLight = state.iconLight,
                iconDark = state.iconDark,
                labelText = state.labelText,
                labelColor = state.labelColor
            )

            ServiceCode(
                code = state.code,
                nextCode = state.nextCode,
                timer = state.timer,
                nextCodeVisible = state.isNextCodeEnabled(showNextCode),
                animateColor = state.authType == ServiceAuthType.Totp,
                modifier = Modifier.weight(1f),
                textStyles = textStyles,
            )

            when (state.authType) {
                ServiceAuthType.Totp -> {
                    ServiceTimer(
                        timer = state.timer,
                        progress = state.progress,
                        textStyles = textStyles,
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
    }
}

@Preview
@Composable
private fun Preview() {
    DsServiceModal(state = ServicePreview)
}
