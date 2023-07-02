package com.twofasapp.feature.externalimport.ui.selector

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.twofasapp.designsystem.R
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.settings.SettingsDescription
import com.twofasapp.designsystem.settings.SettingsHeader
import com.twofasapp.designsystem.settings.SettingsLink
import com.twofasapp.locale.TwLocale

@Composable
internal fun SelectorRoute(
    onGoogleAuthenticatorClick: () -> Unit,
    onAegisClick: () -> Unit,
    onRaivoClick: () -> Unit,
    onLastPassClick: () -> Unit,
) {
    SelectorScreen(
        onGoogleAuthenticatorClick = onGoogleAuthenticatorClick,
        onAegisClick = onAegisClick,
        onRaivoClick = onRaivoClick,
        onLastPassClick = onLastPassClick,
    )
}

@Composable
private fun SelectorScreen(
    onGoogleAuthenticatorClick: () -> Unit,
    onAegisClick: () -> Unit,
    onRaivoClick: () -> Unit,
    onLastPassClick: () -> Unit,
) {

    Scaffold(
        topBar = { TwTopAppBar(TwLocale.strings.externalImportTitle) }
    ) { padding ->

        LazyColumn(modifier = Modifier.padding(top = padding.calculateTopPadding())) {
            item {
                SettingsHeader(title = TwLocale.strings.externalImportHeader)
            }

            item {
                SettingsLink(
                    title = TwLocale.strings.externalImportGoogleAuthenticator,
                    image = painterResource(id = R.drawable.logo_google_authenticator),
                    onClick = onGoogleAuthenticatorClick
                )
            }

            item {
                SettingsLink(
                    title = TwLocale.strings.externalImportAegis,
                    image = painterResource(id = R.drawable.logo_aegis),
                    onClick = onAegisClick
                )
            }

            item {
                SettingsLink(
                    title = TwLocale.strings.externalImportRaivo,
                    image = painterResource(id = R.drawable.logo_raivo),
                    onClick = onRaivoClick
                )
            }

            item {
                SettingsLink(
                    title = TwLocale.strings.externalImportLastPass,
                    image = painterResource(id = R.drawable.logo_lastpass),
                    onClick = onLastPassClick
                )
            }

            item {
                SettingsDescription(text = TwLocale.strings.externalImportNotice)
            }
        }
    }
}