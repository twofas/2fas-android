package com.twofasapp.design.compose.dialogs.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.twofasapp.design.compose.dialogs.SimpleDialog
import com.twofasapp.designsystem.TwTheme

@Composable
internal fun BaseDialogSurface(content: @Composable () -> Unit) {
    val maxWidth = 560.dp
    val maxHeight = if (isLargeDevice()) LocalConfiguration.current.screenHeightDp.dp - 96.dp else 560.dp

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .sizeIn(maxHeight = maxHeight, maxWidth = maxWidth)
            .clipToBounds()
            .wrapContentHeight(),
        shape = RoundedCornerShape(6.dp),
        color = TwTheme.color.surface,
    ) {
        content()
    }
}

@Composable
internal fun BaseDialogIcon(painter: Painter?, tint: Color?, modifier: Modifier) {
    if (painter != null) {
        Spacer(modifier = Modifier.height(24.dp))
        Icon(
            painter = painter,
            tint = tint ?: TwTheme.color.primary,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .then(modifier)
        )
    }
}

@Composable
internal fun BaseDialogTitle(text: String?, center: Boolean = false) {
    val titleModifier = Modifier
        .padding(start = 24.dp, end = 24.dp, top = 26.dp, bottom = 20.dp)
        .fillMaxWidth()
    val showTitle = text.isNullOrBlank().not()

    if (showTitle) {
        Text(
            text = text.orEmpty(),
            style = MaterialTheme.typography.titleLarge.copy(color = TwTheme.color.onSurfacePrimary),
            textAlign = if (center) TextAlign.Center else TextAlign.Start,
            modifier = titleModifier
        )
    } else {
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
internal fun BaseDialogContent(applyContentModifier: Boolean = true, content: @Composable () -> Unit) {
    val contentModifier = Modifier.padding(horizontal = 24.dp)

    if (applyContentModifier) {
        Box(modifier = contentModifier) { content() }
    } else {
        content()
    }
}

@Composable
internal fun BaseDialogTextContent(text: String?, modifier: Modifier = Modifier) {
    if (text.isNullOrBlank().not()) {
        Text(
            text = text.orEmpty(),
            style = MaterialTheme.typography.bodyLarge.copy(color = TwTheme.color.onSurfaceSecondary, letterSpacing = 0.25.sp),
            modifier = modifier,
        )
    }
}

@Composable
internal fun BaseDialogButtons(
    positiveText: String? = null,
    negativeText: String? = null,
    isPositiveEnabled: Boolean = true,
    isNegativeEnabled: Boolean = true,
    topMargin: Boolean = true,
    onPositive: (() -> Unit)? = null,
    onNegative: (() -> Unit)? = null,
    onDismiss: () -> Unit = {},
) {
    val showButtons = onPositive != null || onNegative != null
    val buttonsModifier = Modifier
        .fillMaxWidth()
        .padding(start = 12.dp, end = 12.dp, top = if (topMargin) 16.dp else 0.dp, bottom = 8.dp)

    if (showButtons) {
        Row(modifier = buttonsModifier, horizontalArrangement = Arrangement.End) {
            if (onNegative != null) {
                TextButton(onClick = {
                    onNegative.invoke()
                    onDismiss.invoke()
                }, enabled = isNegativeEnabled) {
                    Text(text = negativeText ?: LocalContext.current.getString(com.twofasapp.resources.R.string.commons__cancel), color = TwTheme.color.primary)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            if (onPositive != null) {
                TextButton(onClick = {
                    onPositive.invoke()
                    onDismiss.invoke()
                }, enabled = isPositiveEnabled) {
                    Text(text = positiveText ?: LocalContext.current.getString(com.twofasapp.resources.R.string.commons__OK), color = TwTheme.color.primary)
                }
            }
        }
    } else {
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun BaseDialog(
    title: String? = null,
    positiveText: String? = null,
    negativeText: String? = null,

    icon: Painter? = null,
    iconTint: Color? = null,

    onPositive: (() -> Unit)? = null,
    onNegative: (() -> Unit)? = null,
    onDismiss: () -> Unit = {},

    applyContentModifier: Boolean = true,
    content: @Composable () -> Unit,
) {

    Dialog(onDismissRequest = onDismiss) {
        BaseDialogSurface {
            Column {
                BaseDialogIcon(icon, iconTint, modifier = Modifier.align(CenterHorizontally))
                BaseDialogTitle(title, center = icon != null)
                BaseDialogContent(applyContentModifier) { content() }
                BaseDialogButtons(
                    positiveText = positiveText,
                    negativeText = negativeText,
                    onPositive = onPositive,
                    onNegative = onNegative,
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun DefaultDialogPreview() {
    SimpleDialog(
        title = "Test dialog",
        text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s",
        onPositive = {},
        onNegative = {}
    )
}

@Composable
internal fun isSmallDevice(): Boolean {
    return LocalConfiguration.current.screenWidthDp <= 360
}

@Composable
internal fun isLargeDevice(): Boolean {
    return LocalConfiguration.current.screenWidthDp <= 600
}