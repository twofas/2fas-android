package com.twofasapp.designsystem.service.atoms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.ResponsiveText
import com.twofasapp.designsystem.service.animateExpireColor

enum class NextCodeGravity { Below, End }

@Composable
internal fun ServiceCode(
    code: String,
    nextCode: String,
    timer: Int,
    animateColor: Boolean = true,
    modifier: Modifier = Modifier,
    nextCodeVisible: Boolean = false,
    nextCodeGravity: NextCodeGravity = NextCodeGravity.Below,
    textStyles: ServiceTextStyle = ServiceTextDefaults.default(),
) {
    val color by animateExpireColor(timer = timer)

    when (nextCodeGravity) {
        NextCodeGravity.Below ->
            Column(
                modifier = modifier,
            ) {
                ResponsiveText(
                    text = code.formatCode(),
                    style = textStyles.codeTextStyle,
                    color = if (animateColor) color else TwTheme.color.onSurfacePrimary,
                    maxLines = 1,
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
                ResponsiveText(
                    text = code.formatCode(),
                    style = textStyles.codeTextStyle,
                    color = if (animateColor) color else TwTheme.color.onSurfacePrimary,
                    maxLines = 1,
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
        timer = 10,
        nextCodeVisible = true
    )
}

@Preview
@Composable
private fun PreviewCompact() {
    ServiceCode(
        code = "123456",
        nextCode = "789987",
        timer = 10,
        nextCodeVisible = true,
        nextCodeGravity = NextCodeGravity.End,
        textStyles = ServiceTextDefaults.compact(),
    )
}