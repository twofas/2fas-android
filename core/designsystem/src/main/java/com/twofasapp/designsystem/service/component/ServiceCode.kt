package com.twofasapp.designsystem.service.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.twofasapp.designsystem.TwTheme

@Composable
fun ServiceCode(
    code: String,
    nextCode: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = code.formatCode(),
        style = TwTheme.typo.h1,
        color = TwTheme.color.onSurfacePrimary,
        maxLines = 1,
        overflow = TextOverflow.Visible,
        modifier = modifier,
    )
}

private fun String.formatCode(): String {
    if (isEmpty()) return ""

    return when (this.length) {
        6 -> "${take(3)} ${takeLast(3)}"
        7 -> "${take(4)} ${takeLast(3)}"
        8 -> "${take(4)} ${takeLast(4)}"
        else -> this
    }
}

@Preview
@Composable
private fun Preview() {
    ServiceCode(code = "123456", nextCode = "789987")
}