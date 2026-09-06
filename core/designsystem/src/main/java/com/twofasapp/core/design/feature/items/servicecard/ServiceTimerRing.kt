package com.twofasapp.core.design.feature.items.servicecard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.animateExpireColor
import com.twofasapp.core.design.foundation.preview.PreviewRow

@Composable
internal fun ServiceTimerRing(
    timer: Int,
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    textStyle: TextStyle = MdtTheme.typo.xs2.medium,
) {
    val color by animateExpireColor(timer = timer)
    val progressFraction by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "",
    )
    val trackColor = MdtTheme.color.onSurface08

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .drawWithCache {
                    val stroke = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)

                    onDrawBehind {
                        drawArc(
                            color = trackColor,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = stroke,
                        )

                        drawArc(
                            color = color,
                            startAngle = -90f,
                            sweepAngle = 360f * progressFraction,
                            useCenter = false,
                            style = stroke,
                        )
                    }
                },
        )

        Text(
            text = timer.toString(),
            style = textStyle,
            color = MdtTheme.color.onSurface,
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    PreviewRow {
        listOf(
            30 to 1f,
            22 to 0.73f,
            15 to 0.5f,
            8 to 0.27f,
            5 to 0.17f,
            1 to 0.03f,
        ).forEach { (timer, progress) ->
            ServiceTimerRing(timer = timer, progress = progress)
        }
    }
}