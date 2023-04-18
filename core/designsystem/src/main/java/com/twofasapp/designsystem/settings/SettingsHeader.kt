package com.twofasapp.designsystem.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme

@Composable
fun SettingsHeader(title: String) {
    Text(
        text = title,
        style = TwTheme.typo.body2,
        color = TwTheme.color.primaryDark,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 72.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 8.dp,
            )
    )
}

@Preview
@Composable
private fun Preview() {
    SettingsHeader(title = "Header")
}