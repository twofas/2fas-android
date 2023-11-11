package com.twofasapp.designsystem.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwTextButton
import com.twofasapp.locale.TwLocale
import kotlinx.coroutines.launch

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
    body: String? = null,
    bodyAnnotated: AnnotatedString? = null,
    positive: String? = null,
    negative: String? = null,
    onPositiveClick: (() -> Unit)? = null,
    onNegativeClick: (() -> Unit)? = null,
    positiveEnabled: Boolean = true,
    negativeEnabled: Boolean = true,
    shape: Shape = TwTheme.shape.dialog,
    containerColor: Color = TwTheme.color.surface,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val showActions = positive != null || negative != null

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        Surface(
            modifier = modifier,
            shape = shape,
            color = containerColor,
        ) {
            Column(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .sizeIn(minWidth = MinWidth, maxWidth = MaxWidth)
                    .padding(
                        top = DialogPadding,
                        bottom = if (showActions) {
                            8.dp
                        } else {
                            DialogPadding
                        }
                    )
            ) {
                if (title != null) {
                    Title(text = title)
                }

                if (body != null || bodyAnnotated != null) {
                    Body(
                        text = body,
                        textAnnotated = bodyAnnotated,
                    )
                }

                content()

                if (showActions) {
                    Actions(
                        positive = positive,
                        negative = negative,
                        onPositiveClick = {
                            scope.launch {
                                onPositiveClick?.invoke()
                                onDismissRequest()
                            }
                        },
                        onNegativeClick = {
                            scope.launch {
                                onNegativeClick?.invoke()
                                onDismissRequest()
                            }
                        },
                        positiveEnabled = positiveEnabled,
                        negativeEnabled = negativeEnabled,
                    )
                }
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
        style = MaterialTheme.typography.headlineSmall,
        color = TwTheme.color.onSurfacePrimary,
        modifier = Modifier
            .padding(horizontal = DialogPadding)
            .padding(TitlePadding)
    )
}

@Composable
private fun ColumnScope.Body(
    text: String?,
    textAnnotated: AnnotatedString?,
) {
    if (text != null) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TwTheme.color.onSurfaceTertiary,
            modifier = Modifier
                .padding(horizontal = DialogPadding)
                .padding(TitlePadding)
                .verticalScroll(rememberScrollState())

        )
    } else if (textAnnotated != null) {
        Text(
            text = textAnnotated,
            style = MaterialTheme.typography.bodyMedium,
            color = TwTheme.color.onSurfaceTertiary,
            modifier = Modifier
                .padding(horizontal = DialogPadding)
                .padding(TitlePadding)
        )
    }
}

@Composable
private fun Actions(
    positive: String? = null,
    negative: String? = null,
    onPositiveClick: (() -> Unit)? = null,
    onNegativeClick: (() -> Unit)? = null,
    positiveEnabled: Boolean = true,
    negativeEnabled: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.End
    ) {
        if (negative != null) {
            TwTextButton(
                text = negative,
                enabled = negativeEnabled,
                onClick = { onNegativeClick?.invoke() }
            )


            Spacer(modifier = Modifier.width(8.dp))
        }
        if (positive != null) {
            TwTextButton(
                text = positive,
                enabled = positiveEnabled,
                onClick = { onPositiveClick?.invoke() }
            )

            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@Preview
@Composable
private fun Preview() {
    BaseDialog(
        onDismissRequest = { },
        title = TwLocale.strings.placeholder,
    )
}


@Preview
@Composable
private fun PreviewButtons() {
    BaseDialog(
        onDismissRequest = { },
        title = TwLocale.strings.placeholder,
        body = TwLocale.strings.placeholderLong,
        positive = "Ok",
        negative = "Cancel",
        positiveEnabled = false,
    )
}