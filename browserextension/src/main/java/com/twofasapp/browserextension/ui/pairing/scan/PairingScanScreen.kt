package com.twofasapp.browserextension.ui.pairing.scan

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.twofasapp.resources.R
import com.twofasapp.design.compose.Toolbar
import com.twofasapp.design.dialogs.InfoDialog
import com.twofasapp.navigation.SettingsDirections
import com.twofasapp.navigation.SettingsRouter
import com.twofasapp.qrscanner.ui.QrScannerScreen
import org.koin.androidx.compose.get

@Composable
internal fun PairingScanScreen(
    viewModel: PairingScanViewModel = get(),
    router: SettingsRouter = get(),
) {
    val uiState = viewModel.uiState.collectAsState()
    val activity = (LocalContext.current as? Activity)

    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(id = R.string.commons__scan_qr_code)) {
                router.navigate(SettingsDirections.GoBack)
            }
        }
    ) { padding ->
        QrScannerScreen()

        if (uiState.value.isSuccess) {
            router.navigate(SettingsDirections.PairingProgress(uiState.value.extensionId))
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