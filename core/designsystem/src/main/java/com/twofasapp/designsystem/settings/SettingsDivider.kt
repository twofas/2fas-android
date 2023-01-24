package com.twofasapp.designsystem.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.twofasapp.designsystem.TwTheme

@Composable
fun SettingsDivider() {
    Divider(
        color = TwTheme.color.divider,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
private fun Preview() {
    SettingsDivider()
}