package com.twofasapp.designsystem.service.atoms

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.service.animateExpireColor

@Composable
internal fun ServiceTimer(
    timer: Int,
    progress: Float,
    modifier: Modifier = Modifier,
    textStyles: ServiceTextStyle = ServiceTextDefaults.default(),
    dimens: ServiceDimens = ServiceDimensDefaults.default(),
) {
    val color by animateExpireColor(timer = timer)
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
            modifier = Modifier.size(dimens.timerSize),
        )

        Text(
            text = timer.toString(),
            style = textStyles.timerTextStyle,
            color = color,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    Column {
        ServiceTimer(timer = 10, progress = 0.33f)
        ServiceTimer(timer = 3, progress = 0.33f)
    }
}