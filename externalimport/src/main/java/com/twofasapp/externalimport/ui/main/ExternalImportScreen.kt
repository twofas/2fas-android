package com.twofasapp.externalimport.ui.main

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twofasapp.design.compose.HeaderEntry
import com.twofasapp.design.compose.SimpleEntry
import com.twofasapp.design.compose.Toolbar
import com.twofasapp.design.theme.textSecondary
import com.twofasapp.resources.R
import com.twofasapp.navigation.ExternalImportDirections
import com.twofasapp.navigation.ExternalImportRouter
import org.koin.androidx.compose.get

@Composable
fun ExternalImportScreen(
    router: ExternalImportRouter = get()
) {
    val activity = LocalContext.current as? Activity

    Scaffold(
        topBar = { Toolbar(title = stringResource(id = R.string.settings__external_import)) { activity?.onBackPressed() } }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            item { HeaderEntry(text = stringResource(id = R.string.externalimport_select_app)) }

            item {
                SimpleEntry(
                    title = stringResource(id = R.string.externalimport_google_authenticator),
                    image = painterResource(id = R.drawable.ic_import_google_authenticator),
                    click = { router.navigate(ExternalImportDirections.GoogleAuthenticator) }
                )

                SimpleEntry(
                    title = stringResource(id = R.string.externalimport_aegis),
                    image = painterResource(id = R.drawable.ic_aegis),
                    click = { router.navigate(ExternalImportDirections.Aegis) }
                )

                SimpleEntry(
                    title = stringResource(id = R.string.externalimport_raivo),
                    image = painterResource(id = R.drawable.ic_raivo),
                    click = { router.navigate(ExternalImportDirections.Raivo) }
                )
            }

            item {
                Text(
                    text = stringResource(id = R.string.externalimport_description),
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.textSecondary,
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(start = 56.dp)
                )
            }

        }
    }
}