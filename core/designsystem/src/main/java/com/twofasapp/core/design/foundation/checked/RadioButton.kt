package com.twofasapp.core.design.foundation.checked

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtTheme

@Composable
fun RadioButton(
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: RadioButtonColors = RadioButtonDefaults.colors(
        selectedColor = MdtTheme.color.primary,
        unselectedColor = MdtTheme.color.onSurfaceSecondary,
    ),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    RadioButton(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
    )
}

@Preview
@Composable
private fun Previews() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        RadioButton(selected = true)
        RadioButton(selected = false)
    }
}