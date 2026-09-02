package com.twofasapp.feature.home.ui.services.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.ButtonStyle
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.screen.EmptyScreen
import com.twofasapp.locale.R

@Composable
internal fun HomeEmpty(
    modifier: Modifier = Modifier,
    onExternalImportClick: () -> Unit = {},
) {
    EmptyScreen(
        modifier = modifier,
        icon = MdtIcons.Qr,
        title = stringResource(id = R.string.tokens__empty_list_title),
        body = stringResource(id = R.string.introduction__description_title),
        additionalContent = {
            Button(
                text = stringResource(id = R.string.introduction__import_external_app),
                style = ButtonStyle.Text,
                onClick = onExternalImportClick,
            )
        },
    )
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        HomeEmpty(
            Modifier.fillMaxSize(),
        )
    }
}