package com.twofasapp.designsystem.service.atoms

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.ktx.assetAsBitmap
import com.twofasapp.designsystem.service.ServiceImageType

@Composable
internal fun ServiceImage(
    type: ServiceImageType,
    iconLight: String,
    iconDark: String,
    labelText: String?,
    labelColor: Color,
    modifier: Modifier = Modifier,
    textStyles: ServiceTextStyle = ServiceTextDefaults.default(),
    dimens: ServiceDimens = ServiceDimensDefaults.default(),
) {
    Box(modifier = modifier) {
        when (type) {
            ServiceImageType.Icon -> {
                Image(
                    bitmap = assetAsBitmap(if (TwTheme.isDark) iconDark else iconLight),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.imageSize)
                )
            }

            ServiceImageType.Label -> {
                Box(
                    modifier = Modifier
                        .size(dimens.imageSize)
                        .clip(TwTheme.shape.circle)
                        .background(labelColor),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(dimens.imageSize.div(1.4f))
                            .height(dimens.imageSize.div(1.9f))
                            .clip(TwTheme.shape.roundedDefault)
                            .background(TwTheme.color.background),
                    )

                    Text(
                        text = labelText.orEmpty(),
                        style = textStyles.imageLabelTextStyle,
                        color = TwTheme.color.onSurfacePrimary,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ServiceImage(
            type = ServiceImageType.Label,
            iconLight = "",
            iconDark = "",
            labelText = "2F",
            labelColor = Color.Red,
            textStyles = ServiceTextDefaults.default(),
            dimens = ServiceDimensDefaults.default(),
        )

        ServiceImage(
            type = ServiceImageType.Label,
            iconLight = "",
            iconDark = "",
            labelText = "2F",
            labelColor = Color.Red,
            textStyles = ServiceTextDefaults.compact(),
            dimens = ServiceDimensDefaults.compact(),
        )
    }
}