package com.twofasapp.designsystem.service.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme

@Composable
internal fun ServiceTimer(
    timer: Int,
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = TwTheme.color.onSurfacePrimary,
) {
    val progressFraction by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = ""
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        CircularProgressIndicator(
            progress = progressFraction,
            color = color,
            strokeWidth = 2.dp,
            modifier = Modifier.size(32.dp),
        )

        Text(
            text = timer.toString(),
            style = TwTheme.typo.caption,
            color = color,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    ServiceTimer(timer = 10, progress = 0.33f)
}