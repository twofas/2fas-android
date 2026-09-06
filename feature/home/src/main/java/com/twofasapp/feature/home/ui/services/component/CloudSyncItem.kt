package com.twofasapp.feature.home.ui.services.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.ButtonHeight
import com.twofasapp.core.design.foundation.button.ButtonStyle
import com.twofasapp.core.design.foundation.icon.Icon
import com.twofasapp.core.design.foundation.other.Space
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.theme.RoundedShape24
import com.twofasapp.locale.MdtLocale

@Composable
internal fun CloudSyncItem(
    modifier: Modifier = Modifier,
    onOpenBackupClick: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 12.dp)
            .padding(bottom = 12.dp, top = 4.dp)
            .clip(RoundedShape24)
            .background(MdtTheme.color.surfaceContainer)
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp, top = 12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MdtTheme.color.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = MdtIcons.CloudOff,
                modifier = Modifier.size(20.dp),
                tint = MdtTheme.color.primary,
            )
        }

        Space(12.dp)

        Text(
            text = MdtLocale.strings.backupSyncNotice,
            color = MdtTheme.color.onSurface,
            style = MdtTheme.typo.base.semiBold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        Space(4.dp)

        Text(
            text = MdtLocale.strings.backupReminderBody,
            color = MdtTheme.color.onSurfaceVariant,
            style = MdtTheme.typo.xs.normal,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        Space(16.dp)

        Button(
            text = MdtLocale.strings.backupSyncCta,
            onClick = onOpenBackupClick,
            style = ButtonStyle.Filled,
            leadingIcon = MdtIcons.CloudUpload,
            size = ButtonHeight.Small,
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    PreviewTheme {
        CloudSyncItem(Modifier.fillMaxWidth())
    }
}