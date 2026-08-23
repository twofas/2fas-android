package com.twofasapp.feature.externalimport.ui.main

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.android.navigation.Navigator
import com.twofasapp.android.navigation.Screen
import com.twofasapp.common.ktx.legacyEncodeBase64ToString
import com.twofasapp.core.design.foundation.permission.RequestPermission
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.screen.CommonContent
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.feature.externalimport.domain.ImportType
import com.twofasapp.feature.externalimport.domain.image
import com.twofasapp.locale.MdtLocale
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
internal fun ExternalImportScreen(
    importType: ImportType,
    viewModel: ExternalImportViewModel = koinViewModel { parametersOf(importType) },
    navigator: Navigator = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        uiState = uiState,
        onScanClick = { navigator.open(Screen.ExternalImportScan(importType = importType.name)) },
        onFilePicked = { encodedFileUri ->
            navigator.open(Screen.ExternalImportResult(importType = importType.name, importFileUri = encodedFileUri))
        },
    )
}

@Composable
private fun Content(
    uiState: ExternalImportUiState,
    onScanClick: () -> Unit = {},
    onFilePicked: (String) -> Unit = {},
) {
    val strings = MdtLocale.strings
    var askForCameraPermission by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { onFilePicked(it.toString().legacyEncodeBase64ToString()) }
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { onFilePicked(it.toString()) } },
    )

    val title = when (uiState.importType) {
        ImportType.GoogleAuthenticator -> strings.externalImportGoogleAuthenticator
        ImportType.Aegis -> strings.externalImportAegis
        ImportType.Raivo -> strings.externalImportRaivo
        ImportType.LastPass -> strings.externalImportLastPass
        ImportType.AuthenticatorPro -> strings.externalImportAuthenticatorPro
        ImportType.AndOtp -> strings.externalImportAndOtp
    }

    val description = when (uiState.importType) {
        ImportType.GoogleAuthenticator -> strings.externalImportGoogleAuthenticatorMsg
        ImportType.Aegis -> strings.externalImportAegisMsg
        ImportType.Raivo -> strings.externalImportRaivoMsg
        ImportType.LastPass -> strings.externalImportLastPassMsg
        ImportType.AuthenticatorPro -> strings.externalImportAuthenticatorProMsg
        ImportType.AndOtp -> strings.externalImportAndOtpMsg
    }

    val ctaPrimary = when (uiState.importType) {
        ImportType.GoogleAuthenticator -> strings.scanQr
        ImportType.Aegis -> strings.externalImportChooseJsonCta
        ImportType.Raivo -> strings.externalImportChooseJsonCta
        ImportType.LastPass -> strings.externalImportChooseJsonCta
        ImportType.AuthenticatorPro -> strings.externalImportChooseTxtCta
        ImportType.AndOtp -> strings.externalImportChooseJsonCta
    }

    val ctaSecondary = when (uiState.importType) {
        ImportType.GoogleAuthenticator -> strings.externalImportChooseQrCta
        else -> null
    }

    Scaffold(
        topBar = { TopAppBar(title) },
    ) { padding ->
        CommonContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            image = painterResource(id = uiState.importType.image),
            descriptionText = description,
            ctaPrimaryText = ctaPrimary,
            ctaSecondaryText = ctaSecondary,
            ctaPrimaryClick = {
                when (uiState.importType) {
                    ImportType.GoogleAuthenticator -> askForCameraPermission = true
                    ImportType.Aegis -> launcher.launch(arrayOf("application/json"))
                    ImportType.Raivo -> launcher.launch(arrayOf("application/json"))
                    ImportType.LastPass -> launcher.launch(arrayOf("application/json"))
                    ImportType.AuthenticatorPro -> launcher.launch(arrayOf("text/*"))
                    ImportType.AndOtp -> launcher.launch(arrayOf("application/json"))
                }
            },
            ctaSecondaryClick = {
                when (uiState.importType) {
                    ImportType.GoogleAuthenticator -> {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    }

                    else -> Unit
                }
            },
        )
    }

    if (askForCameraPermission) {
        RequestPermission(
            permission = Manifest.permission.CAMERA,
            onGranted = {
                askForCameraPermission = false
                onScanClick()
            },
            onDismissRequest = { askForCameraPermission = false },
            rationaleTitle = strings.permissionCameraTitle,
            rationaleText = strings.permissionCameraBody,
        )
    }
}

@Preview
@Composable
private fun PreviewGa() {
    PreviewTheme {
        Content(uiState = ExternalImportUiState())
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        Content(uiState = ExternalImportUiState(importType = ImportType.AuthenticatorPro))
    }
}