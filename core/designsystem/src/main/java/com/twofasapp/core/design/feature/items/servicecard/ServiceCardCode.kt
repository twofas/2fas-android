package com.twofasapp.core.design.feature.items.servicecard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.lerp
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.ServiceAuthType
import com.twofasapp.core.design.feature.items.ServicePreview
import com.twofasapp.core.design.feature.items.ServiceState
import com.twofasapp.core.design.feature.items.animateExpireColor
import com.twofasapp.core.design.feature.items.formatCode
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.ktx.fixedFontSize

@Composable
internal fun ServiceCardCode(
    modifier: Modifier = Modifier,
    state: ServiceState,
    revealed: Boolean,
    showNextCode: Boolean,
    codeTextStyle: TextStyle,
    codeWithNextCodeTextStyle: TextStyle,
    nextCodeTextStyle: TextStyle,
    nextCodeEmphasizedTextStyle: TextStyle,
    nextCodePadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 5.dp),
) {
    val expireColor by animateExpireColor(timer = state.timer)
    val nextCodeEmphasis by animateFloatAsState(
        targetValue = if (revealed && state.isNextCodeEnabled(showNextCode)) 1f else 0f,
        animationSpec = TweenSpec(),
        label = "NextCodeEmphasis",
    )

    val flipRotation by animateFloatAsState(
        targetValue = if (revealed) 0f else 180f,
        animationSpec = TweenSpec(durationMillis = 350),
        label = "RevealFlip",
    )
    val showCodeFace by remember { derivedStateOf { flipRotation <= 90f } }

    val fullCodeStyle = codeTextStyle.fixedFontSize()

    // Height is reserved at the full code size so name/info above do not shift while the code scales.
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val codeRowHeight = remember(fullCodeStyle, density) {
        with(density) { textMeasurer.measure(text = "0", style = fullCodeStyle, maxLines = 1).size.height.toDp() }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(codeRowHeight)
            .graphicsLayer {
                rotationX = flipRotation
                cameraDistance = 12f * this.density
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (showCodeFace) {
            Text(
                text = state.code.formatCode(),
                style = lerp(
                    start = fullCodeStyle,
                    stop = codeWithNextCodeTextStyle.fixedFontSize(),
                    fraction = nextCodeEmphasis,
                ),
                color = when (state.authType) {
                    ServiceAuthType.Totp, ServiceAuthType.Steam -> expireColor
                    ServiceAuthType.Hotp -> MdtTheme.color.onSurface
                },
                maxLines = 1,
            )

            AnimatedVisibility(
                visible = state.isNextCodeEnabled(showNextCode),
            ) {
                NextCodePill(
                    nextCode = state.nextCode,
                    textStyle = lerp(
                        start = nextCodeTextStyle.fixedFontSize(),
                        stop = nextCodeEmphasizedTextStyle.fixedFontSize(),
                        fraction = nextCodeEmphasis,
                    ),
                    contentPadding = nextCodePadding,
                )
            }
        } else {
            HiddenDots(
                formattedCode = state.code.formatCode(),
                modifier = Modifier.graphicsLayer { rotationX = 180f },
            )
        }
    }
}

@Composable
internal fun NextCodePill(
    modifier: Modifier = Modifier,
    nextCode: String,
    textStyle: TextStyle = MdtTheme.typo.xs.medium.fixedFontSize(),
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 5.dp),
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(MdtTheme.color.surfaceContainerHighest)
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = nextCode.formatCode(),
            style = textStyle,
            color = MdtTheme.color.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
internal fun HiddenDots(
    modifier: Modifier = Modifier,
    formattedCode: String,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        formattedCode.map {
            if (it.isWhitespace()) {
                Spacer(modifier = Modifier.width(4.dp))
            } else {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(8.dp)
                        .background(MdtTheme.color.onSurface, CircleShape),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    PreviewTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            listOf(
                Triple(ServicePreview, true, false),
                Triple(ServicePreview.copy(timer = 3), true, true),
                Triple(ServicePreview.copy(code = "12345678", nextCode = "87654321"), true, true),
                Triple(ServicePreview, false, false),
            ).forEach { (state, revealed, showNextCode) ->
                ServiceCardCode(
                    state = state,
                    revealed = revealed,
                    showNextCode = showNextCode,
                    codeTextStyle = (if (state.code.length > 6) MdtTheme.typo.xl4 else MdtTheme.typo.xl5).light,
                    codeWithNextCodeTextStyle = (if (state.code.length > 6) MdtTheme.typo.xl3 else MdtTheme.typo.xl4).light,
                    nextCodeTextStyle = MdtTheme.typo.xs.normal,
                    nextCodeEmphasizedTextStyle = (if (state.code.length > 6) MdtTheme.typo.sm else MdtTheme.typo.base).normal,
                )
            }
        }
    }
}