package com.twofasapp.designsystem.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.locale.TwLocale

@Composable
fun SettingsDescription(text: String) {
    Text(
        text = text,
        style = TwTheme.typo.body3,
        color = TwTheme.color.onSurfaceSecondary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 72.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 16.dp,
            )
    )
}

@Preview
@Composable
private fun Preview() {
    SettingsDescription(text = TwLocale.strings.placeholderLong)
}