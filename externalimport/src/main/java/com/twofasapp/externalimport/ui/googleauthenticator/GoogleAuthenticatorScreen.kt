package com.twofasapp.externalimport.ui.googleauthenticator

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twofasapp.design.compose.ButtonShape
import com.twofasapp.design.compose.ButtonTextColor
import com.twofasapp.design.compose.Toolbar
import com.twofasapp.design.compose.dialogs.RationaleDialog
import com.twofasapp.resources.R
import com.twofasapp.navigation.ExternalImportDirections
import com.twofasapp.navigation.ExternalImportRouter
import com.twofasapp.permissions.CameraPermissionRequestFlow
import com.twofasapp.permissions.PermissionStatus
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import org.koin.androidx.compose.get

@Composable
fun GoogleAuthenticatorScreen(
    router: ExternalImportRouter = get(),
    cameraPermissionRequest: CameraPermissionRequestFlow = get(),
) {
    var showRationaleDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { Toolbar(title = stringResource(id = R.string.externalimport_google_authenticator)) { router.navigateBack() } }
    ) { padding ->

        Column(
            Modifier
                .fillMaxWidth()
                .padding(padding)
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
                    painter = painterResource(id = R.drawable.import_google_authenticator),
                    contentDescription = null,
                    modifier = Modifier.height(100.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Export your accounts from Google Authenticator to a QR code using the \"Transfer Accounts\" option. Then make a screenshot and use the \"Choose QR code\" button below. If you're importing codes from another device, use the \"Scan QR code\" button instead.",
                    style = MaterialTheme.typography.body1.copy(lineHeight = 22.sp, fontSize = 17.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }

            Button(
                onClick = {
                    cameraPermissionRequest.execute()
                        .take(1)
                        .onEach {
                            when (it) {
                                PermissionStatus.GRANTED -> router.navigate(ExternalImportDirections.ImportScan())
                                PermissionStatus.DENIED -> Unit
                                PermissionStatus.DENIED_NEVER_ASK -> showRationaleDialog = true
                            }
                        }.launchIn(coroutineScope)
                },
                shape = ButtonShape(),
                modifier = Modifier
                    .height(48.dp)
                    .align(CenterHorizontally)
            ) {
                Text(text = "Scan QR code".uppercase(), color = ButtonTextColor())
            }

            TextButton(
                onClick = {
                    cameraPermissionRequest.execute()
                        .take(1)
                        .onEach {
                            when (it) {
                                PermissionStatus.GRANTED -> router.navigate(ExternalImportDirections.ImportScan(startWithGallery = true))
                                PermissionStatus.DENIED -> Unit
                                PermissionStatus.DENIED_NEVER_ASK -> showRationaleDialog = true
                            }
                        }.launchIn(coroutineScope)
                },
                shape = ButtonShape(),
                modifier = Modifier
                    .padding(bottom = 16.dp, top = 8.dp)
                    .height(48.dp)
                    .align(CenterHorizontally)
            ) {
                Text(text = "Choose QR code".uppercase())
            }
        }

        if (showRationaleDialog) {
            RationaleDialog(
                title = stringResource(id = R.string.permissions__camera_permission),
                text = stringResource(id = R.string.permissions__camera_permission_description),
                onDismiss = { showRationaleDialog = false }
            )
        }
    }
}