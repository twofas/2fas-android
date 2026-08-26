package com.twofasapp.feature.backup.ui.backup

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.R
import com.twofasapp.core.design.foundation.dialog.RichConfirmDialog
import com.twofasapp.locale.TwLocale

@Composable
internal fun TurnOffConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    RichConfirmDialog(
        onDismissRequest = onDismissRequest,
        image = painterResource(id = R.drawable.illustration_2fas_backup_failed),
        title = TwLocale.strings.backupTurnOffTitle,
        body = TwLocale.strings.backupTurnOffMsg1,
        content = {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = TwLocale.strings.backupTurnOffMsg2,
                textAlign = TextAlign.Center,
                color = MdtTheme.color.onSurfaceSecondary,
                style = MdtTheme.typo.caption,
            )
        },
        positive = TwLocale.strings.backupTurnOffCta,
        negative = TwLocale.strings.commonCancel,
        onPositive = onConfirm,
    )
}

@Preview
@Composable
private fun PreviewTurnOffDialog() {
    TurnOffConfirmationDialog({}, {})
}