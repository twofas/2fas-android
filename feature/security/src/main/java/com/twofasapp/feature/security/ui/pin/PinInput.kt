package com.twofasapp.feature.security.ui.pin

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.preview.PreviewColumn

private val DotSize = 18.dp
private val DotSpacing = 20.dp

private const val BreatheDurationMillis = 1000
private const val BreatheStaggerMillis = 140
private const val BreatheMinAlpha = 0.25f

@Composable
internal fun PinInput(
    modifier: Modifier = Modifier,
    digits: Int,
    enteredDigits: Int,
    loading: Boolean = false,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(DotSpacing),
        modifier = modifier,
    ) {
        repeat(digits) { index ->
            PinDot(
                filled = index < enteredDigits,
                index = index,
                loading = loading,
            )
        }
    }
}

@Composable
private fun PinDot(
    filled: Boolean,
    index: Int,
    loading: Boolean,
    modifier: Modifier = Modifier,
) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(filled) {
        if (filled) {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = 220
                    1f at 0
                    1.2f at 90
                    1f at 220
                },
            )
        }
    }

    val alpha = if (loading) breathingAlpha(index) else 1f

    Box(
        modifier = modifier
            .size(DotSize)
            .scale(scale.value)
            .alpha(alpha)
            .background(
                color = if (filled) MdtTheme.color.primary else MdtTheme.color.transparent,
                shape = CircleShape,
            )
            .then(
                if (filled) {
                    Modifier
                } else {
                    Modifier.border(width = 2.dp, color = MdtTheme.color.outline, shape = CircleShape)
                },
            ),
    )
}

/**
 * Slow "breathing" fade that loops forever, offset by [index] so the dots pulse one after another
 * like a wave. Used while the entered pin is being verified.
 */
@Composable
private fun breathingAlpha(index: Int): Float {
    val transition = rememberInfiniteTransition(label = "PinBreathe")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = BreatheDurationMillis
                1f at 0
                BreatheMinAlpha at BreatheDurationMillis / 2 using FastOutSlowInEasing
                1f at BreatheDurationMillis using FastOutSlowInEasing
            },
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(index * BreatheStaggerMillis),
        ),
        label = "PinBreatheAlpha",
    )
    return alpha
}

@Preview
@Composable
fun PreviewPinInput() {
    PreviewColumn {
        PinInput(
            digits = 4,
            enteredDigits = 1,
        )

        PinInput(
            digits = 6,
            enteredDigits = 3,
        )
    }
}

internal fun notifyInvalidPin(haptic: HapticFeedback) {
    haptic.performHapticFeedback(HapticFeedbackType.Reject)
}