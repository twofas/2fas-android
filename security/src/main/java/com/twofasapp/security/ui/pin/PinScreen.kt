package com.twofasapp.security.ui.pin

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.twofasapp.design.compose.ProgressBar
import com.twofasapp.design.theme.textPrimary
import com.twofasapp.resources.R
import com.twofasapp.security.ui.biometric.BiometricDialog

sealed interface PinScreenState {
    object Loading : PinScreenState
    object Default : PinScreenState
    object Verifying : PinScreenState
}

@Composable
internal fun PinScreen(
    message: String = "",
    errorMessage: String = "",
    digits: Int = 4,
    showLogo: Boolean = false,
    showBiometrics: Boolean = false,
    isEnabled: Boolean = true,
    state: PinScreenState = PinScreenState.Loading,
    currentPinState: CurrentPinState = rememberCurrentPinState(),
    modifier: Modifier = Modifier,
    onPinEntered: (String) -> Unit = {},
    onBiometricsVerified: () -> Unit = {},
) {

    var showBiometricDialog by remember { mutableStateOf(true) }

    if (state == PinScreenState.Loading) {
        Box(modifier = Modifier.fillMaxSize()) {
            ProgressBar(modifer = Modifier.align(Alignment.Center))
        }
    }

    AnimatedVisibility(
        visible = state != PinScreenState.Loading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showLogo) {
                Image(
                    painter = painterResource(id = R.drawable.logo_2fas),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(48.dp)
                )
            }

            Text(
                text = errorMessage.ifBlank { message },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1,
                color = if (errorMessage.isNotBlank()) MaterialTheme.colors.primary else MaterialTheme.colors.textPrimary
            )

            PinInput(
                digits = digits,
                enteredDigits = currentPinState.pin.value.length,
                isVerifying = state == PinScreenState.Verifying,
                onBackClick = {
                    if (currentPinState.pin.value.isNotEmpty()) {
                        currentPinState.pin.value = currentPinState.pin.value.dropLast(1)
                    }
                }
            )

            PinKeyboard(
                showBiometrics = showBiometrics,
                isEnabled = state != PinScreenState.Verifying && isEnabled,
                onBiometricsClick = { showBiometricDialog = true },
                onKeyClick = {
                    if (currentPinState.pin.value.length < digits) {
                        val isFullyEntered = currentPinState.pin.value.length == digits - 1
                        currentPinState.pin.value += it

                        if (isFullyEntered) {
                            onPinEntered.invoke(currentPinState.pin.value)
                        }
                    }
                }
            )
        }
    }

    if (showBiometricDialog && showBiometrics) {
        BiometricDialog(
            activity = LocalContext.current as FragmentActivity,
            onSuccess = {
                onBiometricsVerified.invoke()
                showBiometricDialog = false
            },
            onFailed = { showBiometricDialog = false },
            onError = { showBiometricDialog = false },
            onDismiss = { showBiometricDialog = false },
        ).show()
    }

}

@Stable
internal class CurrentPinState(
    val pin: MutableState<String>,
) {
    fun reset() {
        pin.value = ""
    }
}

@Composable
internal fun rememberCurrentPinState(
    currentPin: MutableState<String> = remember { mutableStateOf("") },
): CurrentPinState = remember {
    CurrentPinState(currentPin)
}

@Composable
@Preview(showSystemUi = true)
fun PreviewPinScreen() {
    PinScreen(
        message = "Please enter your PIN",
        state = PinScreenState.Default,
    )
}
