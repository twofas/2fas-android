package com.twofasapp.feature.security.ui.setuppin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twofasapp.data.session.domain.PinDigits
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.dialog.ListRadioDialog
import com.twofasapp.designsystem.ktx.LocalBackDispatcher
import com.twofasapp.feature.security.ui.pin.PinScreen
import com.twofasapp.feature.security.ui.pin.rememberCurrentPinState
import com.twofasapp.feature.security.ui.pin.vibrateInvalidPin
import com.twofasapp.locale.R
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun SetupPinScreen(
    viewModel: SetupPinViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState().value
    val currentPinState = rememberCurrentPinState()

    var showPinOptionsDialog by remember { mutableStateOf(false) }

    uiState.events.firstOrNull()?.let {
        when (it) {
            SetupPinUiEvent.ClearCurrentPin -> currentPinState.reset()
            SetupPinUiEvent.Finish -> LocalBackDispatcher.onBackPressed()
            SetupPinUiEvent.NotifyInvalidPin -> vibrateInvalidPin(LocalContext.current)
        }

        viewModel.consumeEvent(it)
    }

    Scaffold(
        topBar = {
            TwTopAppBar(titleText = stringResource(id = R.string.security__create_pin))
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            PinScreen(
                message = stringResource(
                    id = uiState.message,
                    when (uiState.digits) {
                        PinDigits.Code4 -> stringResource(id = R.string.settings__pin_4_digits)
                        PinDigits.Code6 -> stringResource(id = R.string.settings__pin_6_digits)
                    }
                ),
                errorMessage = uiState.errorMessage?.let { stringResource(id = it) }.orEmpty(),
                digits = uiState.digits.value,
                showLogo = false,
                showBiometrics = false,
                state = uiState.pinScreenState,
                currentPinState = currentPinState,
                onPinEntered = { viewModel.pinEntered(it) }
            )

            if (uiState.showPinOptions) {
                TextButton(
                    onClick = { showPinOptionsDialog = true }, modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(text = stringResource(id = R.string.settings__select_pin_length))
                }
            }
        }

        if (showPinOptionsDialog) {
            ListRadioDialog(
                options = PinDigits.values().map { stringResource(id = it.label) },
                selectedOption = stringResource(id = uiState.digits.label),
                onDismissRequest = { showPinOptionsDialog = false },
                onOptionSelected = { index, _ ->
                    currentPinState.reset()
                    viewModel.pinDigitsChanged(PinDigits.values()[index])
                }
            )
        }
    }
}