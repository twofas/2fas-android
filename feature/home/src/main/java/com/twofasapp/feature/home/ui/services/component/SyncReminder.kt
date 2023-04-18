package com.twofasapp.feature.home.ui.services.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.R
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwButton
import com.twofasapp.designsystem.common.TwImage
import com.twofasapp.designsystem.common.TwOutlinedButton
import com.twofasapp.locale.TwLocale

@Composable
internal fun SyncReminder(
    modifier: Modifier = Modifier,
    onOpenBackupClick: () -> Unit = {},
    onDismissClick: () -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = TwLocale.strings.backupReminder,
                color = TwTheme.color.onSurfacePrimary,
                style = TwTheme.typo.h3,
                modifier = Modifier.weight(1f)

            )
            TwImage(
                painter = painterResource(id = (R.drawable.logo_google_drive)),
                modifier = Modifier.size(40.dp)
            )

        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = TwLocale.strings.backupReminderBody,
            color = TwTheme.color.onSurfacePrimary,
            style = TwTheme.typo.body1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            TwOutlinedButton(text = TwLocale.strings.backupReminderDismiss, onClick = onDismissClick)
            Spacer(modifier = Modifier.width(8.dp))
            TwButton(text = TwLocale.strings.backupReminderCta, onClick = onOpenBackupClick)
        }
    }

}

@Preview
@Composable
private fun Preview() {
    SyncReminder(Modifier.fillMaxWidth())
}