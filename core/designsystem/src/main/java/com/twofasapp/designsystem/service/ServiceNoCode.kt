package com.twofasapp.designsystem.service

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.service.component.ServiceData
import com.twofasapp.designsystem.service.component.ServiceImage

@Composable
fun ServiceNoCode(
    name: String,
    info: String? = null,
    imageType: ServiceImageType,
    iconLight: String,
    iconDark: String,
    labelText: String?,
    labelColor: Color,
    imageSize: Dp = 36.dp,
    containerColor: Color = TwTheme.color.background,
    modifier: Modifier = Modifier,
    endContent: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .height(64.dp)
            .background(containerColor),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ServiceImage(
            type = imageType,
            iconLight = iconLight,
            iconDark = iconDark,
            labelText = labelText,
            labelColor = labelColor,
            size = imageSize,
        )

        Spacer(Modifier.width(16.dp))

        ServiceData(
            name = name,
            info = info,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(16.dp))

        endContent()
    }
}

@Preview
@Composable
private fun Preview() {
    ServiceNoCode(
        name = "Service Name",
        info = "Additional Info",
        imageType = ServiceImageType.Label,
        iconLight = "Hollie",
        iconDark = "Louisa",
        labelText = "2F",
        labelColor = Color.Red,
        modifier = Modifier.fillMaxWidth(),
        endContent = {}
    )
}