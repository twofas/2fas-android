package com.twofasapp.feature.startup.ui.startupbackup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.screen.CommonContent
import com.twofasapp.locale.TwLocale

@Composable
internal fun StartupBackupScreen(
    openHome: () -> Unit = {},
    openBackup: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TwTheme.color.background)
            .padding(16.dp)
    ) {
        CommonContent(
            modifier = Modifier
                .fillMaxSize(),
            image = painterResource(id = com.twofasapp.designsystem.R.drawable.illustration_2fas_backup),
            descriptionText = TwLocale.strings.startupBackupBody,
            ctaPrimaryText = TwLocale.strings.commonContinue,
            ctaSecondaryText = TwLocale.strings.startupBackupCloseCta,
            ctaPrimaryClick = openBackup,
            ctaSecondaryClick = openHome,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    StartupBackupScreen()
}