package com.twofasapp.design.compose

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme

@Composable
fun ProgressBar(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier, strokeWidth = 4.dp, color = TwTheme.color.primary)
}