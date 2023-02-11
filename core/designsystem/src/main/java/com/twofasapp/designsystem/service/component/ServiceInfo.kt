package com.twofasapp.designsystem.service.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme

@Composable
fun ServiceInfo(
    text: String?,
    modifier: Modifier = Modifier,
    style: TextStyle = TwTheme.typo.body3
) {
    if (text.isNullOrEmpty().not()) {
        Text(
            text = text!!,
            style = style,
            color = TwTheme.color.onSurfaceSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier,
        )
    } else {
        Spacer(Modifier.width(8.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    ServiceInfo(text = "Info")
}