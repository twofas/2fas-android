package com.twofasapp.feature.home.ui.services.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwIcon
import com.twofasapp.designsystem.common.TwTextButton
import com.twofasapp.locale.TwLocale

@Composable
internal fun SyncNoticeBar(
    modifier: Modifier = Modifier,
    onOpenBackupClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .background(TwTheme.color.surface)
            .padding(start = 16.dp, end = 4.dp)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        TwIcon(
            painter = TwIcons.CloudOff,
            tint = TwTheme.color.primary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = TwLocale.strings.backupSyncNotice,
            modifier = Modifier.weight(1f),
            color = TwTheme.color.onSurfacePrimary,
            style = TwTheme.typo.body3,
        )

        Spacer(modifier = Modifier.width(4.dp))

        TwTextButton(
            text = TwLocale.strings.backupSyncCta,
            onClick = onOpenBackupClick,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    SyncNoticeBar(Modifier.fillMaxWidth())
}