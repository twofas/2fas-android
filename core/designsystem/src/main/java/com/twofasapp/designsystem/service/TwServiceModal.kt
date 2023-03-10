package com.twofasapp.designsystem.service

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.service.component.ServiceCode
import com.twofasapp.designsystem.service.component.ServiceImage
import com.twofasapp.designsystem.service.component.ServiceInfo
import com.twofasapp.designsystem.service.component.ServiceName
import com.twofasapp.designsystem.service.component.ServiceTimer

@Composable
fun TwServiceModal(
    state: ServiceState,
    showNextCode: Boolean = false,
    modifier: Modifier = Modifier,
    containerColor: Color = TwTheme.color.background,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .padding(top = 24.dp, bottom = 16.dp)
            .padding(horizontal = 8.dp)
    ) {
        ServiceName(
            text = state.name,
            style = TwTheme.typo.title,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        ServiceInfo(
            text = state.info,
            style = TwTheme.typo.body1,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Row(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp),
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
                nextCodeVisible = state.timer <= 5 && showNextCode,
                modifier = Modifier.weight(1f),
            )

            ServiceTimer(
                timer = state.timer,
                progress = state.progress,
                modifier = Modifier.padding(end = 12.dp),
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    TwServiceModal(state = ServicePreview)
}
