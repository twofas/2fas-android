package com.twofasapp.security.ui.setuppin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twofasapp.design.compose.Toolbar
import com.twofasapp.design.compose.dialogs.ListDialog
import com.twofasapp.navigation.SecurityRouter
import com.twofasapp.resources.R
import com.twofasapp.security.domain.model.PinDigits
import com.twofasapp.security.ui.pin.PinScreen
import com.twofasapp.security.ui.pin.rememberCurrentPinState
import com.twofasapp.security.ui.pin.vibrateInvalidPin
import com.twofasapp.security.ui.security.SecurityViewModel
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@Composable
internal fun SetupPinScreen(
    viewModel: SecurityViewModel = getViewModel(),
    setupViewModel: SetupPinViewModel = get(),
    router: SecurityRouter = get(),
) {
    val uiState = setupViewModel.uiState.collectAsState().value
    val currentPinState = rememberCurrentPinState()

    var showPinOptionsDialog by remember { mutableStateOf(false) }

    uiState.getMostRecentEvent()?.let {
        when (it) {
            SetupPinUiState.Event.ClearCurrentPin -> currentPinState.reset()
            SetupPinUiState.Event.Finish -> router.navigateBack()
            SetupPinUiState.Event.NotifyInvalidPin -> vibrateInvalidPin(LocalContext.current)
        }

        setupViewModel.eventHandled(it.id)
    }

    Scaffold(
        topBar = {
            Toolbar(title = stringResource(id = R.string.security__create_pin)) { router.navigateBack() }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            PinScreen(
                message = stringResource(id = uiState.message, "${uiState.digits.value}-digit"),
                errorMessage = uiState.errorMessage?.let { stringResource(id = it) }.orEmpty(),
                digits = uiState.digits.value,
                showLogo = false,
                showBiometrics = false,
                state = uiState.pinScreenState,
                currentPinState = currentPinState,
                onPinEntered = { setupViewModel.pinEntered(it) }
            )

            if (uiState.showPinOptions) {
                TextButton(
                    onClick = { showPinOptionsDialog = true }, modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(text = "Pin options".uppercase())
                }
            }
        }

        if (showPinOptionsDialog) {
            ListDialog(
                items = PinDigits.values().map { it.label },
                selected = uiState.digits.label,
                onDismiss = { showPinOptionsDialog = false },
                onSelected = { index, _ ->
                    currentPinState.reset()
                    setupViewModel.pinDigitsChanged(PinDigits.values()[index])
                }
            )
        }
    }
}