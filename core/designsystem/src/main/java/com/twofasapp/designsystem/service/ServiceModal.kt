package com.twofasapp.designsystem.service

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
internal fun ServiceModal(
    state: ServiceState,
    containerColor: Color = TwTheme.color.background,
    modifier: Modifier = Modifier,
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

        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(16.dp))

            ServiceImage(
                type = state.imageType,
                iconLight = state.iconLight,
                iconDark = state.iconDark,
                labelText = state.labelText,
                labelColor = state.labelColor
            )

            Spacer(Modifier.width(16.dp))

            ServiceCode(
                code = state.code,
                nextCode = state.nextCode,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(16.dp))

            ServiceTimer(
                timer = state.timer,
                progress = state.progress
            )

            Spacer(Modifier.width(12.dp))
        }
    }

}


@Preview
@Composable
private fun Preview() {
    Service(
        state = ServiceState(
            name = "Service Name",
            info = "Additional Info",
            code = "123456",
            nextCode = "456789",
            timer = 15,
            progress = .5f,
            imageType = ServiceImageType.Label,
            iconLight = "Hollie",
            iconDark = "Louisa",
            labelText = "2F",
            labelColor = Color.Red,
            badgeColor = Color.Red,
        ),
        style = ServiceStyle.Modal,
        modifier = Modifier.fillMaxWidth()
    )
}
