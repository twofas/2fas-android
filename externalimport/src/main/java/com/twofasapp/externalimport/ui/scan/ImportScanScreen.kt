package com.twofasapp.externalimport.ui.scan

import android.app.Activity
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.twofasapp.core.encoding.encodeBase64ToString
import com.twofasapp.design.compose.Toolbar
import com.twofasapp.design.dialogs.InfoDialog
import com.twofasapp.navigation.ExternalImportDirections
import com.twofasapp.navigation.ExternalImportRouter
import com.twofasapp.qrscanner.ui.QrScannerScreen
import org.koin.androidx.compose.get

@Composable
internal fun ImportScanScreen(
    startWithGallery: Boolean,
    viewModel: ImportScanViewModel = get(),
    router: ExternalImportRouter = get(),
) {
    val uiState = viewModel.uiState.collectAsState()
    val activity = (LocalContext.current as? Activity)

    Scaffold(
        topBar = {
            Toolbar(
                title = "Scan QR Code",
            ) {
                router.navigateBack()
            }
        }
    ) { padding ->

        QrScannerScreen(isGalleryEnabled = true, startWithGallery = startWithGallery)

        if (uiState.value.isSuccess) {
            router.navigate(
                ExternalImportDirections.ImportResult(
                    type = ExternalImportDirections.ImportResult.Type.GoogleAuthenticator,
                    content = uiState.value.content.encodeBase64ToString()
                )
            )
        }

        if (uiState.value.showErrorDialog) {
            InfoDialog(
                context = activity!!,
                titleRes = uiState.value.errorDialogTitle,
                msgRes = uiState.value.errorDialogMessage,
                closeAction = uiState.value.errorDialogAction,
            ).show()
        }
    }
}