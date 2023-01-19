package com.twofasapp.design.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AnimatedContent(
    condition: Boolean,
    contentWhenTrue: @Composable () -> Unit,
    contentWhenFalse: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = condition.not(),
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier,
    ) {
        contentWhenFalse.invoke()
    }

    AnimatedVisibility(
        visible = condition,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier,
    ) {
        contentWhenTrue.invoke()
    }
}