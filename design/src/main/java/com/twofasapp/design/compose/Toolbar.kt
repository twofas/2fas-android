package com.twofasapp.design.compose

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.twofasapp.design.theme.textPrimary
import com.twofasapp.design.theme.toolbar
import com.twofasapp.design.theme.toolbarContent


@Composable
fun Toolbar(
    title: String,
    backgroundColor: Color = MaterialTheme.colors.toolbar,
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    navigationClick: () -> Unit
) {
    TopAppBar(
        title = { Text(text = title, color = MaterialTheme.colors.textPrimary) },
        navigationIcon = {
            IconButton(onClick = { navigationClick.invoke() }) {
                Icon(Icons.Filled.ArrowBack, null, tint = MaterialTheme.colors.toolbarContent)
            }
        },
        actions = actions,
        backgroundColor = backgroundColor,
        contentColor = MaterialTheme.colors.toolbarContent,
        elevation = elevation,
        modifier = modifier
    )
}

@Preview
@Composable
internal fun PreviewToolbar() {
    Toolbar(title = "Test") {}
}