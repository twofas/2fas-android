package com.twofasapp.feature.security.ui.setuppin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.android.navigation.Navigator
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.ButtonStyle
import com.twofasapp.core.design.foundation.dialog.ListRadioDialog
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.data.session.domain.PinDigits
import com.twofasapp.feature.security.ui.pin.SettingsPinScreen
import com.twofasapp.feature.security.ui.pin.notifyInvalidPin
import com.twofasapp.locale.R
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
internal fun SetupPinScreen(
    viewModel: SetupPinViewModel = koinViewModel(),
    navigator: Navigator = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.finished) {
        if (uiState.finished) {
            navigator.back()
        }
    }

    Content(
        uiState = uiState,
        onKeyClick = viewModel::onKeyClick,
        onBackspaceClick = viewModel::onBackspaceClick,
        onPinDigitsChanged = viewModel::onPinDigitsChanged,
    )
}

@Composable
private fun Content(
    uiState: SetupPinUiState,
    onKeyClick: (Int) -> Unit = {},
    onBackspaceClick: () -> Unit = {},
    onPinDigitsChanged: (PinDigits) -> Unit = {},
) {
    val haptic = LocalHapticFeedback.current
    var showPinOptionsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.invalidPinCount) {
        if (uiState.invalidPinCount > 0) {
            notifyInvalidPin(haptic)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = stringResource(id = R.string.security__create_pin)) },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            SettingsPinScreen(
                digits = uiState.digits.value,
                enteredDigits = uiState.enteredPin.length,
                message = stringResource(
                    id = uiState.message,
                    stringResource(id = uiState.digits.label),
                ),
                errorMessage = uiState.errorMessage?.let { stringResource(id = it) }.orEmpty(),
                enabled = !uiState.verifying,
                loading = uiState.verifying,
                onKeyClick = onKeyClick,
                onBackspaceClick = onBackspaceClick,
            )

            if (uiState.showPinOptions) {
                Button(
                    style = ButtonStyle.Text,
                    text = stringResource(id = R.string.settings__select_pin_length),
                    onClick = { showPinOptionsDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                )
            }
        }

        if (showPinOptionsDialog) {
            ListRadioDialog(
                options = PinDigits.entries.map { stringResource(id = it.label) },
                selectedOption = stringResource(id = uiState.digits.label),
                onDismissRequest = { showPinOptionsDialog = false },
                onOptionSelected = { index, _ ->
                    onPinDigitsChanged(PinDigits.entries[index])
                },
            )
        }
    }
}

@Composable
@Preview
private fun Preview() {
    PreviewTheme {
        Content(
            uiState = SetupPinUiState(),
        )
    }
}