package com.twofasapp.feature.home.ui.services

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.common.TwOutlinedButton
import com.twofasapp.feature.home.R
import com.twofasapp.locale.TwLocale

@Composable
internal fun ServicesEmpty(
    modifier: Modifier = Modifier,
    onExternalImportClick: () -> Unit = {},
) {
    Column(
        modifier = modifier.padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.img_services_empty), null, modifier = Modifier.height(120.dp))
        Text(text = TwLocale.strings.servicesEmptyBody, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        TwOutlinedButton(text = TwLocale.strings.servicesEmptyImportCta, onClick = onExternalImportClick)
    }
}