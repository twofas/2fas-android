package com.twofasapp.core.design.feature.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.twofasapp.core.design.MdtTheme

@Composable
fun SettingsDivider() {
    HorizontalDivider(
        color = MdtTheme.color.divider,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview
@Composable
private fun Preview() {
    SettingsDivider()
}