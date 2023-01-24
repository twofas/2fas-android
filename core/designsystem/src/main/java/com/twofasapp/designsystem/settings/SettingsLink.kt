package com.twofasapp.designsystem.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.locale.TwLocale

@Composable
fun SettingsLink(
    title: String,
    icon: Painter? = null,
    image: Painter? = null,
    textColor: Color = TwTheme.color.onSurfacePrimary,
    modifier: Modifier = Modifier,
    showEmptySpaceWhenIconMissing: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick != null) { onClick?.invoke() }
            .height(56.dp)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(painter = icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = TwTheme.color.primary)
        } else if (image != null)
            Image(painter = image, contentDescription = null, modifier = Modifier.size(24.dp))
        else if (showEmptySpaceWhenIconMissing) {
            Spacer(modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.size(24.dp))

        Text(
            text = title,
            style = TwTheme.typo.body1,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
    }

}

@Preview
@Composable
private fun Preview() {
    SettingsLink(
        title = TwLocale.strings.placeholder,
        icon = TwIcons.Placeholder,
    )
}