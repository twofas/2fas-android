package com.twofasapp.designsystem.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme

@Composable
fun ModalList(
    content: @Composable () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(TwTheme.color.surface)
            .padding(vertical = 16.dp)
    ) {
        content()
    }
}

@Composable
fun ModalListItem(
    text: String,
    icon: Painter,
    iconTint: Color = TwTheme.color.primary,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(TwTheme.color.surface)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {

        Icon(icon, null, tint = iconTint, modifier = Modifier.size(24.dp))

        Spacer(Modifier.width(16.dp))

        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview
@Composable
private fun Preview() {
    ModalList {
        ModalListItem(text = "Test 1", icon = TwIcons.Placeholder)
        ModalListItem(text = "Test 2", icon = TwIcons.Placeholder)
        ModalListItem(text = "Test 3", icon = TwIcons.Placeholder)
    }
}