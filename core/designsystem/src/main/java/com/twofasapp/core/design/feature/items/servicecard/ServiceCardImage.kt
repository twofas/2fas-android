package com.twofasapp.core.design.feature.items.servicecard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.ServiceImageType
import com.twofasapp.core.design.foundation.image.AsyncImage
import com.twofasapp.core.design.foundation.preview.PreviewRow

@Composable
internal fun ServiceCardImage(
    modifier: Modifier = Modifier,
    type: ServiceImageType,
    iconLight: String,
    iconDark: String,
    labelText: String?,
    labelColor: Color,
) {
    when (type) {
        ServiceImageType.Icon -> {
            AsyncImage(
                model = "file:///android_asset/${if (MdtTheme.isDark) iconDark else iconLight}",
                modifier = modifier,
            )
        }

        ServiceImageType.Label -> {
            val textMeasurer = rememberTextMeasurer()
            val text = labelText.orEmpty().uppercase()
            val textColor = if (labelColor.luminance() > 0.5f) Color.Black else Color.White
            val textStyle = MdtTheme.typo.sm.bold

            Box(
                modifier = modifier
                    .clip(CircleShape)
                    .background(labelColor)
                    .drawWithCache {
                        // Font size is fixed in dp: px -> sp via toSp() cancels out the user font scale.
                        val fontSize = (size.height / 2.5f).toSp()
                        val textLayout = textMeasurer.measure(
                            text = text,
                            style = textStyle.copy(fontSize = fontSize, lineHeight = fontSize, fontWeight = FontWeight.ExtraBold),
                        )
                        onDrawBehind {
                            drawText(
                                textLayoutResult = textLayout,
                                color = textColor,
                                topLeft = Offset(
                                    x = (size.width - textLayout.size.width) / 2f,
                                    y = (size.height - textLayout.size.height) / 2f,
                                ),
                            )
                        }
                    },
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    PreviewRow {
        listOf(20.dp, 24.dp, 28.dp, 32.dp, 36.dp, 40.dp, 44.dp, 48.dp, 52.dp).forEach { size ->
            ServiceCardImage(
                type = ServiceImageType.Label,
                iconLight = "",
                iconDark = "",
                labelText = "2F",
                labelColor = MdtTheme.color.primary,
                modifier = Modifier.size(size),
            )
        }
    }
}