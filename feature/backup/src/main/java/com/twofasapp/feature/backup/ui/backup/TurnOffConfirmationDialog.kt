package com.twofasapp.feature.backup.ui.backup

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.R
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwButton
import com.twofasapp.designsystem.common.TwTextButton
import com.twofasapp.designsystem.dialog.BaseDialog
import com.twofasapp.locale.TwLocale

@Composable
internal fun TurnOffConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    BaseDialog(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.illustration_2fas_backup_failed),
                contentDescription = null,
                modifier = Modifier.height(100.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = TwLocale.strings.backupTurnOffTitle,
                textAlign = TextAlign.Center,
                color = TwTheme.color.onSurfacePrimary,
                style = TwTheme.typo.title,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = TwLocale.strings.backupTurnOffMsg1,
                textAlign = TextAlign.Center,
                color = TwTheme.color.onSurfacePrimary,
                style = TwTheme.typo.body3,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = TwLocale.strings.backupTurnOffMsg2,
                textAlign = TextAlign.Center,
                color = TwTheme.color.onSurfaceSecondary,
                style = TwTheme.typo.caption,
            )

            Spacer(modifier = Modifier.height(32.dp))

            TwButton(
                text = TwLocale.strings.backupTurnOffCta,
                modifier = Modifier,
                onClick = {
                    onDismissRequest()
                    onConfirm()
                },
            )
            TwTextButton(
                text = TwLocale.strings.commonCancel,
                modifier = Modifier,
                onClick = onDismissRequest,
            )
        }
    }

}

@Preview
@Composable
private fun PreviewTurnOffDialog() {
    TurnOffConfirmationDialog({}, {})
}