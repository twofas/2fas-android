package com.twofasapp.feature.security.ui.pin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.other.Space
import com.twofasapp.core.design.foundation.preview.PreviewTheme

/**
 * Stateless PIN pad used by the settings flows (setup / change / disable).
 * All state is owned by the caller's ViewModel; this composable only renders it and forwards key presses.
 */
@Composable
internal fun SettingsPinScreen(
    digits: Int,
    enteredDigits: Int,
    modifier: Modifier = Modifier,
    message: String = "",
    errorMessage: String = "",
    enabled: Boolean = true,
    loading: Boolean = false,
    onKeyClick: (Int) -> Unit = {},
    onBackspaceClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .safeContentPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = errorMessage.ifBlank { message },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MdtTheme.typo.sm.normal,
            color = if (errorMessage.isNotBlank()) MdtTheme.color.error else MdtTheme.color.onSurface,
        )

        Space(40.dp)

        PinInput(
            digits = digits,
            enteredDigits = enteredDigits,
            loading = loading,
        )

        Space(48.dp)

        PinKeyboard(
            showBiometrics = false,
            enabled = enabled,
            onKeyClick = onKeyClick,
            onBackspaceClick = onBackspaceClick,
        )
    }
}

@Composable
@Preview
private fun Preview() {
    PreviewTheme {
        SettingsPinScreen(
            digits = 4,
            enteredDigits = 2,
            message = "Enter your PIN",
        )
    }
}