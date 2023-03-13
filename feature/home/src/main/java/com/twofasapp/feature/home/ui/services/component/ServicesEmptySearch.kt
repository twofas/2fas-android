package com.twofasapp.feature.home.ui.services.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.twofasapp.designsystem.screen.CommonContent
import com.twofasapp.feature.home.R
import com.twofasapp.locale.TwLocale

@Composable
internal fun ServicesEmptySearch(
    modifier: Modifier = Modifier,
) {
    CommonContent(
        image = painterResource(id = R.drawable.img_services_empty_search),
        titleText = TwLocale.strings.servicesEmptySearch,
        descriptionText = TwLocale.strings.servicesEmptySearchBody,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun Preview() {
    ServicesEmptySearch(Modifier.fillMaxSize())
}