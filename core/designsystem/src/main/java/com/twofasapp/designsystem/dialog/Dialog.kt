package com.twofasapp.designsystem.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.twofasapp.designsystem.TwTheme

internal val DialogPadding = 24.dp
private val IconPadding = PaddingValues(bottom = 16.dp)
private val TitlePadding = PaddingValues(bottom = 16.dp)
private val TextPadding = PaddingValues(bottom = 24.dp)

private val MinWidth = 280.dp
private val MaxWidth = 560.dp

@Composable
fun BaseDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    shape: Shape = TwTheme.shape.dialog,
    containerColor: Color = TwTheme.color.surface,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties()
    ) {
        Surface(
            modifier = modifier,
            shape = shape,
            color = containerColor,
        ) {
            Column(
                modifier = Modifier
                    .sizeIn(minWidth = MinWidth, maxWidth = MaxWidth)
                    .padding(top = DialogPadding, bottom = DialogPadding)
            ) {
                if (title != null) {
                    Title(text = title)
                }

                content()
            }
        }
    }
}

@Composable
private fun Title(
    text: String,
) {
    Text(
        text = text,
        style = TwTheme.typo.title,
        modifier = Modifier
            .padding(horizontal = DialogPadding)
            .padding(TitlePadding)
    )
}