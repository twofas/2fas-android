package com.twofasapp.designsystem.composable

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwsTheme

@Composable
fun TwsNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    NavigationBar(
        containerColor = TwsTheme.color.background,
        tonalElevation = 4.dp,
        modifier = modifier,
        content = content,
    )
}

@Composable
fun RowScope.TwsNavigationBarItem(
    text: String,
    icon: Painter,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        label = { Text(text) },
        icon = { Icon(painter = icon, contentDescription = null) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = TwsTheme.color.primary,
            selectedTextColor = TwsTheme.color.primary,
            indicatorColor = TwsTheme.color.background,
            unselectedIconColor = TwsTheme.color.onSurfaceDarker,
            unselectedTextColor = TwsTheme.color.onSurfaceDarker,
        ),
        modifier = modifier,
    )
}