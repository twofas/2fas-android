package com.twofasapp.designsystem.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.twofasapp.designsystem.R
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwButton
import com.twofasapp.designsystem.common.TwTextButton
import com.twofasapp.locale.TwLocale

@Composable
fun RichConfirmDialog(
    onDismissRequest: () -> Unit,
    image: Painter? = null,
    title: String? = null,
    body: String? = null,
    content: @Composable (() -> Unit)? = null,
    positive: String? = TwLocale.strings.commonYes,
    negative: String? = TwLocale.strings.commonNo,
    onPositive: () -> Unit = {},
    onNegative: () -> Unit = {},
    properties: DialogProperties = DialogProperties(),
) {
    BaseDialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (image != null) {
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier.height(88.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (title != null) {
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    color = TwTheme.color.onSurfacePrimary,
                    style = TwTheme.typo.title,
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (body != null) {
                Text(
                    text = body,
                    textAlign = TextAlign.Center,
                    color = TwTheme.color.onSurfacePrimary,
                    style = TwTheme.typo.body3,
                )
            }

            content?.invoke()

            Spacer(modifier = Modifier.height(32.dp))

            if (positive != null) {
                TwButton(
                    text = positive,
                    modifier = Modifier,
                    height = 42.dp,
                    onClick = {
                        onDismissRequest()
                        onPositive()
                    },
                )
            }

            if (negative != null) {
                TwTextButton(
                    text = negative,
                    modifier = Modifier,
                    onClick = {
                        onDismissRequest()
                        onNegative()
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    RichConfirmDialog(
        onDismissRequest = { },
        image = painterResource(id = R.drawable.illustration_2fas_backup_failed),
        title = TwLocale.strings.placeholderMedium,
        body = TwLocale.strings.placeholderLong,
    )
}