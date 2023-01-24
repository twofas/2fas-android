package com.twofasapp.designsystem.service.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
fun ServiceData(
    name: String,
    info: String?,
    code: String? = null,
    nextCode: String? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        ServiceName(name)
        ServiceInfo(info)

        if (code != null && nextCode != null) {
            ServiceCode(code = code, nextCode = nextCode)
        }
    }
}

@Composable
fun ServiceName(
    text: String,
    style: TextStyle = TwTheme.typo.body3,
    modifier: Modifier = Modifier
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

@Composable
fun ServiceInfo(
    text: String?,
    style: TextStyle = TwTheme.typo.body3,
    modifier: Modifier = Modifier
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
    ServiceData(
        name = "Service Name",
        info = "test@mail.com",
        modifier = Modifier.fillMaxWidth()
    )
}