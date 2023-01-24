package com.twofasapp.browserextension.ui.pairing.scan

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.twofasapp.design.dialogs.InfoDialog
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.qrscanner.ui.QrScannerScreen
import com.twofasapp.resources.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun PairingScanScreen(
    openPairingProgress: (String) -> Unit,
    viewModel: PairingScanViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState()
    val activity = (LocalContext.current as? Activity)
    var openSuccess by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TwTopAppBar(titleText = stringResource(id = R.string.commons__scan_qr_code), modifier = Modifier.clickable { viewModel.pairMockedBrowser() }, showBackButton = true)
        }
    ) { padding ->
        QrScannerScreen()

        if (uiState.value.isSuccess && openSuccess) {
            openSuccess = false
            openPairingProgress(uiState.value.extensionId)
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