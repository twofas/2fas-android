package com.twofasapp.core.design.foundation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.icon.Icon
import com.twofasapp.core.design.foundation.other.Space
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.locale.MdtLocale

@Composable
fun EmptyScreen(
    icon: Painter,
    modifier: Modifier = Modifier,
    title: String? = null,
    body: String? = null,
    additionalContent: (@Composable ColumnScope.() -> Unit)? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = icon,
            tint = MdtTheme.color.primary,
            modifier = Modifier.size(64.dp),
        )

        if (title != null) {
            Space(16.dp)

            Text(
                text = title,
                style = MdtTheme.typo.xl.medium,
                color = MdtTheme.color.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (body != null) {
            Space(16.dp)

            Text(
                text = body,
                style = MdtTheme.typo.base.normal,
                color = MdtTheme.color.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (additionalContent != null) {
            Space(24.dp)

            additionalContent()
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        EmptyScreen(
            icon = MdtIcons.Info,
            title = MdtLocale.strings.placeholder,
            body = MdtLocale.strings.placeholderMedium,
            modifier = Modifier.fillMaxSize(),
        )
    }
}