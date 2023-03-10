package com.twofasapp.designsystem.service.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme

enum class NextCodeGravity { Below, End }

@Composable
fun ServiceCode(
    code: String,
    nextCode: String,
    nextCodeVisible: Boolean = false,
    nextCodeGravity: NextCodeGravity = NextCodeGravity.Below,
    color: Color = TwTheme.color.onSurfacePrimary,
    style: TextStyle = TwTheme.typo.h1.copy(fontWeight = FontWeight.ExtraLight),
    modifier: Modifier = Modifier,
) {

    when (nextCodeGravity) {
        NextCodeGravity.Below ->
            Column(
                modifier = modifier,
            ) {
                Text(
                    text = code.formatCode(),
                    style = style,
                    color = color,
                    maxLines = 1,
                    overflow = TextOverflow.Visible,
                )

                AnimatedVisibility(visible = nextCodeVisible) {
                    Text(
                        text = nextCode.formatCode(),
                        style = TwTheme.typo.body2,
                        maxLines = 1,
                        overflow = TextOverflow.Visible,
                    )
                }
            }

        NextCodeGravity.End ->
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = code.formatCode(),
                    style = style,
                    color = color,
                    maxLines = 1,
                    overflow = TextOverflow.Visible,
                )

                AnimatedVisibility(visible = nextCodeVisible) {
                    Text(
                        text = nextCode.formatCode(),
                        style = TwTheme.typo.body2,
                        maxLines = 1,
                        overflow = TextOverflow.Visible,
                        modifier = Modifier.padding(bottom = 2.dp, start = 8.dp)
                    )
                }
            }
    }
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
    ServiceCode(
        code = "123456",
        nextCode = "789987",
        nextCodeVisible = true
    )
}

@Preview
@Composable
private fun PreviewCompact() {
    ServiceCode(
        code = "123456",
        nextCode = "789987",
        nextCodeVisible = true,
        nextCodeGravity = NextCodeGravity.End,
        style = TwTheme.typo.h3.copy(fontWeight = FontWeight.Light)
    )
}