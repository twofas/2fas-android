package com.twofasapp.security.ui.pin

import android.content.Context
import android.os.Vibrator
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.design.theme.divider
import com.twofasapp.resources.R

@Composable
internal fun PinInput(
    digits: Int,
    enteredDigits: Int,
    isVerifying: Boolean = false,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 24.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Row(modifier = Modifier.align(Alignment.Center)) {
                repeat(digits) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(12.dp)
                            .run {
                                if (index < enteredDigits) {
                                    background(shape = CircleShape, color = MaterialTheme.colors.primary)

                                } else {
                                    background(shape = CircleShape, color = MaterialTheme.colors.background)
                                    border(width = 2.dp, color = MaterialTheme.colors.divider, shape = CircleShape)
                                }
                            }

                    )
                }
            }

            if (isVerifying) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(20.dp)
                )
            } else {
                IconButton(onClick = {
                    if (enteredDigits > 0) {
                        onBackClick()
                    }
                }, modifier = Modifier.align(Alignment.CenterEnd)) {
                    Icon(
                        painterResource(id = R.drawable.ic_backspace), null,
                        tint = if (enteredDigits == 0) {
                            Color(0x80606060)
                        } else {
                            Color(0xFF606060)
                        }
                    )
                }
            }
        }

        Divider(color = MaterialTheme.colors.divider)
    }
}

@Composable
@Preview(showSystemUi = true)
fun PreviewPinInput() {
    PinInput(
        digits = 4,
        enteredDigits = 1,
        isVerifying = false,
        onBackClick = {}
    )
}

internal fun vibrateInvalidPin(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    vibrator.vibrate(longArrayOf(0, 200, 20, 30), -1)
}