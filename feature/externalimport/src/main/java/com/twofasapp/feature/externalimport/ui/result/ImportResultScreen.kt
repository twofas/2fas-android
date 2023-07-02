package com.twofasapp.feature.externalimport.ui.result

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwButton
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.feature.externalimport.domain.ExternalImport
import com.twofasapp.feature.externalimport.navigation.ImportType
import com.twofasapp.feature.externalimport.ui.common.ImportDescription
import com.twofasapp.resources.R
import org.koin.androidx.compose.get

@Composable
internal fun ImportResultRoute(
    type: String,
    content: String,
    onFinish: () -> Unit,
    viewModel: ImportResultViewModel = get()
) {
    val uiState = viewModel.uiState.collectAsState().value
    val activity = (LocalContext.current as? Activity)
    val importType = ImportType.valueOf(type)

    LaunchedEffect(Unit) {
        viewModel.import(importType, content)
    }

    if (uiState.finishSuccess) {
        Toast.makeText(activity, activity?.getString(R.string.backup__import_completed_successfuly), Toast.LENGTH_LONG).show()
        onFinish()
    }

    Scaffold(
        topBar = { TwTopAppBar(stringResource(id = R.string.settings__external_import)) }
    ) { padding ->

        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = padding.calculateTopPadding())
        ) {

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp)
            ) {
                Image(
                    painter = painterResource(
                        id = when (importType) {
                            ImportType.GoogleAuthenticator -> R.drawable.ic_import_ga
                            ImportType.Aegis -> R.drawable.ic_import_aegis
                            ImportType.Raivo -> R.drawable.ic_import_raivo
                            ImportType.LastPass -> R.drawable.ic_import_lastpass
                            ImportType.AuthenticatorPro -> R.drawable.ic_import_authenticatorpro
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.height(120.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (uiState.title != null) {
                    Text(
                        text = stringResource(id = uiState.title),
                        style = TwTheme.typo.title,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.description != null) {
                    ImportDescription(text = stringResource(id = uiState.description))
                }

                if (uiState.servicesToImport != 0 && uiState.totalServicesCount != 0) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (uiState.servicesToImport == uiState.totalServicesCount) {
                            uiState.servicesToImport.toString()
                        } else {
                            stringResource(id = R.string.tokens__google_auth_out_of_title).format(uiState.servicesToImport, uiState.totalServicesCount)
                        },
                        style = TwTheme.typo.h3,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                if (uiState.footer != null) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(id = uiState.footer),
                        style = TwTheme.typo.body1,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }

            TwButton(
                text = uiState.button?.let { stringResource(id = uiState.button) } ?: "",
                onClick = {
                    if (uiState.importResult is ExternalImport.Success) {
                        viewModel.saveServices()
                    } else {
                        activity?.onBackPressed()
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .height(48.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}
