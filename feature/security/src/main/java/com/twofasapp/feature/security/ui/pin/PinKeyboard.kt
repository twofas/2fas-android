package com.twofasapp.feature.security.ui.pin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.preview.PreviewTheme

private val KeyHeight = 64.dp
private val KeyPadding = 8.dp

private sealed interface PinKey {
    data class Digit(val value: Int) : PinKey
    data object Backspace : PinKey
    data object Biometrics : PinKey
    data object Empty : PinKey
}

private fun pinKeys(showBiometrics: Boolean): List<PinKey> = buildList {
    for (digit in 1..9) add(PinKey.Digit(digit))
    add(if (showBiometrics) PinKey.Biometrics else PinKey.Empty)
    add(PinKey.Digit(0))
    add(PinKey.Backspace)
}

@Composable
internal fun PinKeyboard(
    enabled: Boolean = true,
    showBiometrics: Boolean = true,
    onKeyClick: (Int) -> Unit = {},
    onBackspaceClick: () -> Unit = {},
    onBiometricsClick: () -> Unit = {},
) {
    val keys = remember(showBiometrics) { pinKeys(showBiometrics) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        userScrollEnabled = false,
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        items(items = keys, key = { it.toString() }) { key ->
            PinKeyButton(
                key = key,
                enabled = enabled,
                onClick = when (key) {
                    is PinKey.Digit -> ({ onKeyClick(key.value) })
                    PinKey.Backspace -> onBackspaceClick
                    PinKey.Biometrics -> onBiometricsClick
                    PinKey.Empty -> null
                },
            )
        }
    }
}

@Composable
private fun PinKeyButton(
    modifier: Modifier = Modifier,
    key: PinKey,
    enabled: Boolean,
    onClick: (() -> Unit)?,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(KeyPadding)
            .height(KeyHeight)
            .clip(CircleShape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(enabled = enabled, onClick = onClick)
                } else {
                    Modifier
                },
            ),
    ) {
        when (key) {
            PinKey.Empty -> Unit

            PinKey.Biometrics -> Icon(
                painter = MdtIcons.Fingerprint,
                contentDescription = null,
                tint = MdtTheme.color.onSurface,
                modifier = Modifier.alpha(if (enabled) 1f else 0.5f),
            )

            PinKey.Backspace -> Icon(
                painter = MdtIcons.Backspace,
                contentDescription = null,
                tint = MdtTheme.color.onSurface,
                modifier = Modifier.alpha(if (enabled) 1f else 0.5f),
            )

            is PinKey.Digit -> Text(
                text = key.value.toString(),
                color = MdtTheme.color.onSurface,
                fontSize = 34.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.alpha(if (enabled) 1f else 0.5f),
            )
        }
    }
}

@Preview
@Composable
fun PreviewPinKeyboard() {
    PreviewTheme {
        PinKeyboard(
            enabled = true,
        )
    }
}