package com.twofasapp.security.ui.disablepin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.navigation.SecurityRouter
import com.twofasapp.resources.R
import com.twofasapp.security.ui.pin.PinScreen
import com.twofasapp.security.ui.pin.rememberCurrentPinState
import com.twofasapp.security.ui.pin.vibrateInvalidPin
import org.koin.androidx.compose.get

@Composable
internal fun DisablePinScreen(
    viewModel: DisablePinViewModel = get(),
    router: SecurityRouter = get(),
) {
    val uiState = viewModel.uiState.collectAsState().value
    val currentPinState = rememberCurrentPinState()

    uiState.getMostRecentEvent()?.let {
        when (it) {
            DisablePinUiState.Event.ClearCurrentPin -> currentPinState.reset()
            DisablePinUiState.Event.Finish -> router.navigateBack()
            DisablePinUiState.Event.NotifyInvalidPin -> vibrateInvalidPin(LocalContext.current)
        }

        viewModel.eventHandled(it.id)
    }

    Scaffold(
        topBar = {
            TwTopAppBar(titleText = stringResource(id = R.string.security__disable_pin))
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            PinScreen(
                message = stringResource(id = R.string.security__enter_current_pin),
                errorMessage = uiState.errorMessage?.let { stringResource(id = it) }.orEmpty(),
                digits = uiState.digits.value,
                showLogo = false,
                showBiometrics = false,
                state = uiState.pinScreenState,
                currentPinState = currentPinState,
                onPinEntered = { viewModel.pinEntered(it) }
            )
        }
    }
}