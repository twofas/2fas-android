package com.twofasapp.feature.security.ui.lock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.twofasapp.feature.security.biometric.BiometricKeyProvider
import com.twofasapp.data.session.domain.LockMethod
import com.twofasapp.feature.security.ui.pin.PinScreen
import com.twofasapp.feature.security.ui.pin.rememberCurrentPinState
import com.twofasapp.feature.security.ui.pin.vibrateInvalidPin
import com.twofasapp.locale.R
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
internal fun LockScreen(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    viewModel: LockViewModel = koinViewModel(),
    biometricKeyProvider: BiometricKeyProvider = koinInject(),
    onSuccess: () -> Unit = {},
) {
    val uiState = viewModel.uiState.collectAsState().value
    val currentPinState = rememberCurrentPinState()
    val onResume by rememberUpdatedState { viewModel.refreshBlockTimeLeft() }

    uiState.events.firstOrNull()?.let {
        when (it) {
            LockUiEvent.ClearCurrentPin -> currentPinState.reset()
            LockUiEvent.Finish -> onSuccess.invoke()
            LockUiEvent.NotifyInvalidPin -> vibrateInvalidPin(LocalContext.current)
        }

        viewModel.consumeEvent(it)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onResume()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    PinScreen(
        message = stringResource(id = R.string.security__enter_pin),
        errorMessage = when {
            uiState.invalidPinStatus.shouldBlock -> stringResource(
                id = R.string.security__too_many_attempts_try_again_after,
                uiState.invalidPinStatus.timeLeftMin.toString()
            )

            uiState.errorMessage != null -> stringResource(id = uiState.errorMessage)
            else -> ""
        },
        digits = uiState.digits.value,
        showLogo = true,
        showBiometrics = uiState.lockMethod == LockMethod.Biometrics,
        isEnabled = uiState.invalidPinStatus.shouldBlock.not(),
        state = uiState.pinScreenState,
        currentPinState = currentPinState,
        onPinEntered = { viewModel.pinEntered(it) },
        onBiometricsVerified = { viewModel.biometricsVerified() },
        onBiometricsInvalidated = {
            biometricKeyProvider.deleteSecretKey()
            viewModel.disableBiometric()
        },
        biometricKeyProvider = biometricKeyProvider,
    )
}
