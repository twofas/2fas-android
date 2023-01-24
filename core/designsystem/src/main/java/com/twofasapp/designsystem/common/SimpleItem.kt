package com.twofasapp.designsystem.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.locale.TwLocale

@Composable
fun SimpleItem(
    title: String = "",
    subtitle: String? = null,
    image: Painter? = null,
    icon: Painter? = null,
    iconTint: Color = TwTheme.color.iconTint,
    iconVisibleWhenNotSet: Boolean = true,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(56.dp, Dp.Infinity)
            .clickable(enabled) { onClick?.invoke() }
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
        verticalArrangement = Arrangement.Center
    ) {

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (image != null) {
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            }

            if (icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp),
                )
            }

            if (image == null && icon == null && iconVisibleWhenNotSet) {
                Spacer(modifier = Modifier.width(24.dp))
            }

            Spacer(modifier = Modifier.width(24.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 17.sp, color = TwTheme.color.onSurfacePrimary),
                modifier = Modifier.weight(1f)
            )
        }

        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 15.sp, color = TwTheme.color.onSurfaceSecondary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp, top = 4.dp)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    SimpleItem(
        title = TwLocale.strings.placeholder,
        subtitle = TwLocale.strings.placeholderLong,
        icon = TwIcons.Placeholder
    )
}