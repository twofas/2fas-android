package com.twofasapp.designsystem.service.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.ktx.assetAsBitmap
import com.twofasapp.designsystem.service.ServiceImageType


@Composable
fun ServiceImage(
    type: ServiceImageType,
    iconLight: String,
    iconDark: String,
    labelText: String?,
    labelColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
) {
    Box(modifier = modifier) {
        when (type) {
            ServiceImageType.Icon -> {
                Image(
                    bitmap = assetAsBitmap(if (TwTheme.isDark) iconDark else iconLight),
                    contentDescription = null,
                    modifier = Modifier.size(size)
                )
            }

            ServiceImageType.Label -> {
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(TwTheme.shape.circle)
                        .background(labelColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = labelText.orEmpty(),
                        style = TwTheme.typo.body2,
                        color = Color.White,
                        modifier = Modifier.offset(x = (-0.8).dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ServiceImage(
        type = ServiceImageType.Label,
        iconLight = "",
        iconDark = "",
        labelText = "2F",
        labelColor = Color.Red
    )
}