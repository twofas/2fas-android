package com.twofasapp.design.compose

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProgressBar(modifer: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifer, strokeWidth = 4.dp)
}