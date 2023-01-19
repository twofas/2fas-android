package com.twofasapp.design.compose

import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun Fab(
    iconVector: ImageVector,
    modifier: Modifier = Modifier,
    click: () -> Unit = {},
) {
    FloatingActionButton(onClick = { click.invoke() }, modifier = modifier, backgroundColor = MaterialTheme.colors.primary) {
        Icon(iconVector, "", tint = Color.White)
    }
}