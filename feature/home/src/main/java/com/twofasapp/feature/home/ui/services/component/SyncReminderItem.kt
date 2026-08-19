package com.twofasapp.feature.home.ui.services.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.TextButton
import com.twofasapp.locale.TwLocale

@Composable
internal fun SyncReminderItem(
    modifier: Modifier = Modifier,
    onOpenBackupClick: () -> Unit = {},
    onDismissClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MdtTheme.color.surface)
            .padding(all = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MdtTheme.color.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = MdtIcons.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MdtTheme.color.primary,
                )
            }

            Spacer(Modifier.size(16.dp))

            Column {
                Text(
                    text = TwLocale.strings.backupReminder,
                    style = MdtTheme.typo.regular.base.copy(fontWeight = FontWeight.Medium),
                    color = MdtTheme.color.onSurface,
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = TwLocale.strings.backupReminderBody,
                    style = MdtTheme.typo.regular.sm,
                    color = MdtTheme.color.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                text = TwLocale.strings.backupReminderDismiss,
                onClick = onDismissClick,
            )

            Button(
                text = TwLocale.strings.backupReminderCta,
                height = 36.dp,
                leadingIcon = MdtIcons.CloudUpload,
                onClick = onOpenBackupClick,
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    SyncReminderItem(Modifier.fillMaxWidth())
}