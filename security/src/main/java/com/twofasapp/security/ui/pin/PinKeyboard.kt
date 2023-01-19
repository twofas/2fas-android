package com.twofasapp.security.ui.pin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twofasapp.design.theme.textPrimary
import com.twofasapp.resources.R

internal enum class Keys {
    Key1,
    Key2,
    Key3,
    Key4,
    Key5,
    Key6,
    Key7,
    Key8,
    Key9,
    Empty,
    Key0,
    Biometrics,
    Empty2,
    ;

    fun toKeyInt(): Int {
        return try {
            name.replace("Key", "").toInt()
        } catch (e: Exception) {
            -1
        }
    }
}

@Composable
internal fun PinKeyboard(
    isEnabled: Boolean = true,
    showBiometrics: Boolean = true,
    onKeyClick: (Int) -> Unit = {},
    onBiometricsClick: () -> Unit = {},
) {

    val alpha = if (isEnabled) 1f else 0.5f
    var keys = Keys.values().toList()

    keys = if (showBiometrics.not()) {
        keys.minus(Keys.Biometrics)
    } else {
        keys.minus(Keys.Empty2)
    }

    LazyVerticalGrid(
        userScrollEnabled = false,
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        items(keys, key = { it.name }) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .height(64.dp)
                    .clip(CircleShape)
                    .run {
                        if (it == Keys.Biometrics) {
                            clickable(isEnabled) { onBiometricsClick() }
                        } else if (it != Keys.Empty) {
                            clickable(isEnabled) { onKeyClick(it.toKeyInt()) }
                        } else {
                            this
                        }
                    }
            ) {
                if (it == Keys.Biometrics) {
                    Icon(
                        painterResource(id = R.drawable.key_fingerprint),
                        null,
                        tint = MaterialTheme.colors.textPrimary,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .alpha(alpha),
                    )

                } else if (it != Keys.Empty && it != Keys.Empty2) {
                    Text(
                        text = it.toKeyInt().toString(),
                        fontSize = 34.sp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .alpha(alpha),
                        color = MaterialTheme.colors.textPrimary,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun PreviewPinKeyboard() {
    PinKeyboard(
        isEnabled = true
    )
}
