package com.twofasapp.designsystem.service.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.twofasapp.designsystem.TwTheme

@Composable
fun ServiceName(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TwTheme.typo.body3
) {
    Text(
        text = text,
        style = style,
        color = TwTheme.color.onSurfacePrimary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun Preview() {
    ServiceName(text = "Name")
}