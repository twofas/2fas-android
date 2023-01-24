package com.twofasapp.design.compose

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.twofasapp.designsystem.TwTheme


@Composable
fun Toolbar(
    title: String,
    backgroundColor: Color = TwTheme.color.background,
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    navigationClick: () -> Unit
) {
    TopAppBar(
        title = { Text(text = title, color = TwTheme.color.onSurfacePrimary) },
        navigationIcon = {
            IconButton(onClick = { navigationClick.invoke() }) {
                Icon(Icons.Filled.ArrowBack, null, tint = TwTheme.color.onSurfacePrimary)
            }
        },
        actions = actions,
        backgroundColor = backgroundColor,
        contentColor = TwTheme.color.onSurfacePrimary,
        elevation = elevation,
        modifier = modifier
    )
}

@Preview
@Composable
internal fun PreviewToolbar() {
    Toolbar(title = "Test") {}
}