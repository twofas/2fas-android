package com.twofasapp.designsystem.service

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import com.twofasapp.designsystem.service.component.ServiceBadge
import com.twofasapp.designsystem.service.component.ServiceCode
import com.twofasapp.designsystem.service.component.ServiceImage
import com.twofasapp.designsystem.service.component.ServiceInfo
import com.twofasapp.designsystem.service.component.ServiceName
import com.twofasapp.designsystem.service.component.ServiceTimer

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ServiceDefault(
    state: ServiceState,
    modifier: Modifier = Modifier,
    containerColor: Color = TwTheme.color.background,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(128.dp)
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
        )

        Column(modifier = Modifier.weight(1f)) {
            ServiceName(state.name)
            ServiceInfo(state.info)
            ServiceCode(
                code = state.code,
                nextCode = state.nextCode,
            )
        }

        ServiceTimer(
            timer = state.timer,
            progress = state.progress,
            modifier = Modifier.padding(end = 12.dp),
        )
    }
}

@Preview
@Composable
private fun Preview() {
    ServiceDefault(state = ServicePreview)
}
