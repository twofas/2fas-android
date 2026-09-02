package com.twofasapp.feature.trash.ui.trash.component

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
internal fun TrashEmpty(
    modifier: Modifier = Modifier,
) {
    EmptyScreen(
        modifier = modifier,
        icon = MdtIcons.Delete,
        title = stringResource(id = R.string.settings__trash_is_empty),
    )
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        TrashEmpty(
            Modifier.fillMaxSize(),
        )
    }
}