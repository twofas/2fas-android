package com.twofasapp.feature.externalimport.ui.selector

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.android.navigation.Navigator
import com.twofasapp.android.navigation.Screen
import com.twofasapp.core.design.R
import com.twofasapp.core.design.feature.settings.OptionEntry
import com.twofasapp.core.design.feature.settings.OptionHeader
import com.twofasapp.core.design.feature.settings.OptionHeaderContentPaddingFirst
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.feature.externalimport.domain.ImportType
import com.twofasapp.locale.MdtLocale
import org.koin.compose.koinInject

@Composable
internal fun ExternalImportSelectorScreen(
    navigator: Navigator = koinInject(),
) {
    Content(
        onImportTypeSelected = { navigator.open(Screen.ExternalImport(importType = it.name)) },
    )
}

@Composable
private fun Content(
    onImportTypeSelected: (ImportType) -> Unit = {},
) {
    Scaffold(
        topBar = { TopAppBar(MdtLocale.strings.externalImportTitle) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            item {
                OptionEntry(
                    title = null,
                    subtitle = MdtLocale.strings.externalImportNotice,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                )
            }

            item {
                OptionHeader(
                    text = MdtLocale.strings.externalImportHeader,
                    contentPadding = OptionHeaderContentPaddingFirst,
                )
            }

            item {
                OptionEntry(
                    title = MdtLocale.strings.externalImportGoogleAuthenticator,
                    image = painterResource(id = R.drawable.logo_google_authenticator),
                    onClick = { onImportTypeSelected(ImportType.GoogleAuthenticator) },
                )
            }

            item {
                OptionEntry(
                    title = MdtLocale.strings.externalImportAegis,
                    image = painterResource(id = R.drawable.logo_aegis),
                    onClick = { onImportTypeSelected(ImportType.Aegis) },
                )
            }

            item {
                OptionEntry(
                    title = MdtLocale.strings.externalImportRaivo,
                    image = painterResource(id = R.drawable.logo_raivo),
                    onClick = { onImportTypeSelected(ImportType.Raivo) },
                )
            }

            item {
                OptionEntry(
                    title = MdtLocale.strings.externalImportLastPass,
                    image = painterResource(id = R.drawable.logo_lastpass),
                    onClick = { onImportTypeSelected(ImportType.LastPass) },
                )
            }

            item {
                OptionEntry(
                    title = MdtLocale.strings.externalImportAuthenticatorPro,
                    image = painterResource(id = R.drawable.logo_authenticatorpro),
                    onClick = { onImportTypeSelected(ImportType.AuthenticatorPro) },
                )
            }

            item {
                OptionEntry(
                    title = MdtLocale.strings.externalImportAndOtp,
                    image = painterResource(id = R.drawable.logo_andotp),
                    onClick = { onImportTypeSelected(ImportType.AndOtp) },
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        Content()
    }
}