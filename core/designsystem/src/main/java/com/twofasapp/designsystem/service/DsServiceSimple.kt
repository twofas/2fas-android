package com.twofasapp.designsystem.service

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.service.atoms.ServiceBadge
import com.twofasapp.designsystem.service.atoms.ServiceImage
import com.twofasapp.designsystem.service.atoms.ServiceInfo
import com.twofasapp.designsystem.service.atoms.ServiceName

@Composable
fun DsServiceSimple(
    state: ServiceState,
    modifier: Modifier = Modifier,
    showBadge: Boolean = false,
    containerColor: Color = TwTheme.color.background,
    content: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .height(64.dp)
            .background(containerColor),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (showBadge) {
            ServiceBadge(
                color = state.badgeColor,
            )
        }

        ServiceImage(
            type = state.imageType,
            iconLight = state.iconLight,
            iconDark = state.iconDark,
            labelText = state.labelText,
            labelColor = state.labelColor,
        )

        Column(Modifier.weight(1f)) {
            ServiceName(text = state.name)
            ServiceInfo(text = state.info, spacer = false)
        }

        content()
    }
}

@Preview
@Composable
private fun Preview() {
    DsServiceSimple(
        state = ServicePreview,
        modifier = Modifier.fillMaxWidth()
    ) {}
}