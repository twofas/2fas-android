package com.twofasapp.designsystem.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme

@Composable
fun TwNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    NavigationBar(
        tonalElevation = 0.dp,
        modifier = modifier,
        content = content,
    )
}

@Composable
fun RowScope.TwNavigationBarItem(
    text: String,
    icon: Painter,
    selected: Boolean,
    showBadge: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        label = { Text(text, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        icon = {
            BadgedBox(badge = {
                if (showBadge) {
                    Badge(containerColor = TwTheme.color.primary)
                }
            }) {
                Icon(painter = icon, contentDescription = null)
            }

        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = TwTheme.color.primary,
            selectedTextColor = TwTheme.color.primary,
            indicatorColor = TwTheme.color.primaryIndicator,
            unselectedIconColor = TwTheme.color.onSurfaceSecondary,
            unselectedTextColor = TwTheme.color.onSurfaceSecondary,
        ),
        modifier = modifier,
    )
}