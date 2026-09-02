package com.twofasapp.feature.home.ui.notifications.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.screen.EmptyScreen
import com.twofasapp.locale.R

@Composable
internal fun NotificationsEmpty(
    modifier: Modifier = Modifier,
) {
    EmptyScreen(
        modifier = modifier,
        icon = MdtIcons.Notification,
        title = stringResource(id = R.string.notifications__no_notifications),
    )
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        NotificationsEmpty(
            Modifier.fillMaxSize(),
        )
    }
}