package com.twofasapp.ui.error

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.ButtonStyle
import com.twofasapp.core.design.ktx.copyToClipboard

@Composable
internal fun ErrorScreen(
    title: String,
    message: String,
    details: String?,
    onClose: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MdtTheme.color.background)
            .safeContentPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
    ) {
        Text(
            text = title,
            style = MdtTheme.typo.xl2.semiBold,
            color = MdtTheme.color.onSurface,
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = message,
            style = MdtTheme.typo.sm.normal,
            color = MdtTheme.color.onSurfaceVariant,
        )

        if (details != null) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = "Error details",
                style = MdtTheme.typo.sm.semiBold,
                color = MdtTheme.color.onSurface,
            )

            Spacer(Modifier.height(8.dp))

            SelectionContainer {
                Text(
                    text = details,
                    style = MdtTheme.typo.xs2.normal.copy(fontFamily = FontFamily.Monospace),
                    color = MdtTheme.color.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MdtTheme.color.surfaceContainer,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .horizontalScroll(rememberScrollState())
                        .padding(12.dp),
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                text = "Copy error details",
                style = ButtonStyle.Filled,
                modifier = Modifier.fillMaxWidth(),
                onClick = { context.copyToClipboard(details) },
            )
        }

        Spacer(Modifier.height(8.dp))

        Button(
            text = "Contact support",
            style = ButtonStyle.Text,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                runCatching {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, "https://2fas.com/help-center".toUri())
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    )
                }
            },
        )

        Spacer(Modifier.height(8.dp))

        Button(
            text = "Close app",
            style = ButtonStyle.Text,
            modifier = Modifier.fillMaxWidth(),
            onClick = onClose,
        )
    }
}