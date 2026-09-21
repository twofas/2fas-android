package com.twofasapp.feature.home.ui.services.component

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
internal fun HomeSearchEmpty(
    modifier: Modifier = Modifier,
) {
    EmptyScreen(
        modifier = modifier,
        icon = MdtIcons.SearchOff,
        title = stringResource(id = R.string.tokens__service_not_found_search),
        body = stringResource(id = R.string.tokens__try_different_search_term),
    )
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        HomeSearchEmpty(
            Modifier.fillMaxSize(),
        )
    }
}