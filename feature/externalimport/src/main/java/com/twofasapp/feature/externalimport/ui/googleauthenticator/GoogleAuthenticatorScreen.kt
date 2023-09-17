package com.twofasapp.feature.externalimport.ui.googleauthenticator

import android.Manifest
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.common.RequestPermission
import com.twofasapp.designsystem.common.TwButton
import com.twofasapp.designsystem.common.TwTextButton
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.feature.externalimport.ui.common.ImportDescription
import com.twofasapp.locale.TwLocale
import com.twofasapp.resources.R

@Composable
internal fun GoogleAuthenticatorRoute(
    onScanClick: (Boolean) -> Unit,
) {
    var askForPermission by remember { mutableStateOf(false) }
    Scaffold(
        topBar = { TwTopAppBar(stringResource(id = R.string.externalimport_google_authenticator)) }
    ) { padding ->

        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = padding.calculateTopPadding())
        ) {

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_import_ga),
                    contentDescription = null,
                    modifier = Modifier.height(120.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                ImportDescription(text = stringResource(id = R.string.introduction__google_authenticator_import_process))
            }

            TwButton(
                text = stringResource(id = R.string.commons__scan_qr_code),
                onClick = { askForPermission = true },
                modifier = Modifier
                    .height(48.dp)
                    .align(CenterHorizontally)
            )

            TwTextButton(
                text = stringResource(id = R.string.introduction__choose_qr_code),
                onClick = { onScanClick(true) },
                modifier = Modifier
                    .padding(bottom = 16.dp, top = 8.dp)
                    .height(48.dp)
                    .align(CenterHorizontally)
            )
        }

        if (askForPermission) {
            RequestPermission(
                permission = Manifest.permission.CAMERA,
                onGranted = {
                    askForPermission = false
                    onScanClick(false)
                },
                onDismissRequest = { askForPermission = false },
                rationaleTitle = TwLocale.strings.permissionCameraTitle,
                rationaleText = TwLocale.strings.permissionCameraBody,
            )
        }
    }
}