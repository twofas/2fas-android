package com.twofasapp.feature.externalimport.ui.selector

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.twofasapp.designsystem.R
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.settings.SettingsDescription
import com.twofasapp.designsystem.settings.SettingsHeader
import com.twofasapp.designsystem.settings.SettingsLink
import com.twofasapp.feature.externalimport.domain.ImportType
import com.twofasapp.locale.TwLocale

@Composable
internal fun ExternalImportSelectorScreen(
    onImportTypeSelected: (ImportType) -> Unit = {},
) {
    Scaffold(
        topBar = { TwTopAppBar(TwLocale.strings.externalImportTitle) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                SettingsHeader(title = TwLocale.strings.externalImportHeader)
            }

            item {
                SettingsLink(
                    title = TwLocale.strings.externalImportGoogleAuthenticator,
                    image = painterResource(id = R.drawable.logo_google_authenticator),
                    onClick = { onImportTypeSelected(ImportType.GoogleAuthenticator) }
                )
            }

            item {
                SettingsLink(
                    title = TwLocale.strings.externalImportAegis,
                    image = painterResource(id = R.drawable.logo_aegis),
                    onClick = { onImportTypeSelected(ImportType.Aegis) }
                )
            }

            item {
                SettingsLink(
                    title = TwLocale.strings.externalImportRaivo,
                    image = painterResource(id = R.drawable.logo_raivo),
                    onClick = { onImportTypeSelected(ImportType.Raivo) }
                )
            }

            item {
                SettingsLink(
                    title = TwLocale.strings.externalImportLastPass,
                    image = painterResource(id = R.drawable.logo_lastpass),
                    onClick = { onImportTypeSelected(ImportType.LastPass) }
                )
            }

            item {
                SettingsLink(
                    title = TwLocale.strings.externalImportAuthenticatorPro,
                    image = painterResource(id = R.drawable.logo_authenticatorpro),
                    onClick = { onImportTypeSelected(ImportType.AuthenticatorPro) }
                )
            }

            item {
                SettingsLink(
                    title = TwLocale.strings.externalImportAndOtp,
                    image = painterResource(id = R.drawable.logo_andotp),
                    onClick = { onImportTypeSelected(ImportType.AndOtp) }
                )
            }

            item {
                SettingsDescription(text = TwLocale.strings.externalImportNotice)
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ExternalImportSelectorScreen()
}
