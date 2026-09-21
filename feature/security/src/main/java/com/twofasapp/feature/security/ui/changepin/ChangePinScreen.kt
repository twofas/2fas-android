package com.twofasapp.feature.security.ui.changepin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.android.navigation.Navigator
import com.twofasapp.android.navigation.Screen
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.feature.security.ui.pin.SettingsPinScreen
import com.twofasapp.feature.security.ui.pin.notifyInvalidPin
import com.twofasapp.locale.R
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
internal fun ChangePinScreen(
    viewModel: ChangePinViewModel = koinViewModel(),
    navigator: Navigator = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.currentPinVerified) {
        if (uiState.currentPinVerified) {
            navigator.popTo(Screen.Security, inclusive = false)
            navigator.open(Screen.SetupPin)
        }
    }

    Content(
        uiState = uiState,
        onKeyClick = viewModel::onKeyClick,
        onBackspaceClick = viewModel::onBackspaceClick,
    )
}

@Composable
private fun Content(
    uiState: ChangePinUiState,
    onKeyClick: (Int) -> Unit = {},
    onBackspaceClick: () -> Unit = {},
) {
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(uiState.invalidPinCount) {
        if (uiState.invalidPinCount > 0) {
            notifyInvalidPin(haptic)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = stringResource(id = R.string.security__change_pin)) },
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
                message = stringResource(id = R.string.security__enter_current_pin),
                errorMessage = uiState.errorMessage?.let { stringResource(id = it) }.orEmpty(),
                enabled = !uiState.verifying,
                loading = uiState.verifying,
                onKeyClick = onKeyClick,
                onBackspaceClick = onBackspaceClick,
            )
        }
    }
}

@Composable
@Preview
private fun Preview() {
    PreviewTheme {
        Content(
            uiState = ChangePinUiState(),
        )
    }
}