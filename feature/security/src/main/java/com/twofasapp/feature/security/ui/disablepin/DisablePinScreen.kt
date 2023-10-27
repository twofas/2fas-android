package com.twofasapp.feature.security.ui.disablepin

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
import com.twofasapp.designsystem.ktx.LocalBackDispatcher
import com.twofasapp.feature.security.ui.pin.PinScreen
import com.twofasapp.feature.security.ui.pin.rememberCurrentPinState
import com.twofasapp.feature.security.ui.pin.vibrateInvalidPin
import com.twofasapp.locale.R
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun DisablePinScreen(
    viewModel: DisablePinViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState().value
    val currentPinState = rememberCurrentPinState()

    uiState.events.firstOrNull()?.let {
        when (it) {
            DisablePinUiEvent.ClearCurrentPin -> currentPinState.reset()
            DisablePinUiEvent.Finish -> LocalBackDispatcher.onBackPressed()
            DisablePinUiEvent.NotifyInvalidPin -> vibrateInvalidPin(LocalContext.current)
        }

        viewModel.consumeEvent(it)
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