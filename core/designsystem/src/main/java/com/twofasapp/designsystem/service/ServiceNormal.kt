package com.twofasapp.designsystem.service

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.service.component.ServiceBadge
import com.twofasapp.designsystem.service.component.ServiceData
import com.twofasapp.designsystem.service.component.ServiceImage
import com.twofasapp.designsystem.service.component.ServiceTimer

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ServiceNormal(
    state: ServiceState,
    containerColor: Color = TwTheme.color.background,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(128.dp)
            .clip(TwTheme.shape.roundedDefault)
            .background(containerColor)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() },
            )
            .border(1.dp, TwTheme.color.surfaceVariant, TwTheme.shape.roundedDefault),

        verticalAlignment = Alignment.CenterVertically
    ) {
        ServiceBadge(color = state.badgeColor)

        Spacer(Modifier.width(16.dp))

        ServiceImage(
            type = state.imageType,
            iconLight = state.iconLight,
            iconDark = state.iconDark,
            labelText = state.labelText,
            labelColor = state.labelColor
        )

        Spacer(Modifier.width(16.dp))

        ServiceData(
            name = state.name,
            info = state.info,
            code = state.code,
            nextCode = state.code,
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
        style = ServiceStyle.Normal,
        modifier = Modifier.fillMaxWidth()
    )
}
