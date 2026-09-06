package com.twofasapp.core.design.feature.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.servicecard.ServiceCardContainer
import com.twofasapp.core.design.feature.items.servicecard.ServiceCardImage
import com.twofasapp.core.design.foundation.checked.CheckIcon
import com.twofasapp.core.design.foundation.preview.PreviewColumn

@Composable
fun ServiceCardSimple(
    modifier: Modifier = Modifier,
    state: ServiceState,
    containerColor: Color = MdtTheme.color.surfaceContainer,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit = {},
) {
    ServiceCardContainer(
        modifier = modifier.height(64.dp),
        badgeColor = state.badgeColor,
        containerColor = containerColor,
        onClick = onClick,
        onLongClick = onLongClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ServiceCardImage(
                modifier = Modifier.size(28.dp),
                type = state.imageType,
                iconLight = state.iconLight,
                iconDark = state.iconDark,
                labelText = state.labelText,
                labelColor = state.labelColor,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = state.name,
                    style = MdtTheme.typo.xs.medium,
                    color = MdtTheme.color.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (state.info.isNullOrEmpty().not()) {
                    Text(
                        text = state.info,
                        style = MdtTheme.typo.xs2.normal,
                        color = MdtTheme.color.onSurfaceVariant.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            content()
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    PreviewColumn {
        ServiceCardSimple(
            state = ServicePreview,
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
        )

        ServiceCardSimple(
            state = ServicePreview,
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
        ) {
            CheckIcon(checked = true)
        }

        ServiceCardSimple(
            state = ServicePreview.copy(name = "Service with a very long name that does not fit", info = null),
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
        ) {
            Switch(checked = true, onCheckedChange = {})
        }
    }
}