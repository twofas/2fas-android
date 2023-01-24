package com.twofasapp.feature.externalimport.ui.scan

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.twofasapp.core.encoding.encodeBase64ToString
import com.twofasapp.design.dialogs.InfoDialog
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.qrscanner.ui.QrScannerScreen
import org.koin.androidx.compose.get

@Composable
internal fun ImportScanRoute(
    startFromGallery: Boolean,
    viewModel: ImportScanViewModel = get(),
    onScanned: (String) -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState()
    val activity = (LocalContext.current as? Activity)

    Scaffold(
        topBar = { TwTopAppBar("Scan QR Code") }
    ) { padding ->

        QrScannerScreen(isGalleryEnabled = true, startWithGallery = startFromGallery, modifier = Modifier.padding(padding))

        if (uiState.value.isSuccess) {
            onScanned(uiState.value.content.encodeBase64ToString())
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