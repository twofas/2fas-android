package com.twofasapp.feature.home.ui.services.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.twofasapp.core.design.foundation.progress.CircularProgressIndicator

@Composable
internal fun ServicesProgress(
    modifier: Modifier = Modifier,
) {
    Box(modifier, Alignment.Center) {
        CircularProgressIndicator()
    }
}