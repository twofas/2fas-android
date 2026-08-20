package com.twofasapp.core.design.foundation.navigationbar

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
import com.twofasapp.core.design.MdtTheme

@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        tonalElevation = 0.dp,
        modifier = modifier,
        content = content,
        containerColor = MdtTheme.color.surface,
    )
}

@Composable
fun RowScope.NavigationBarItem(
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
                    Badge(containerColor = MdtTheme.color.primary)
                }
            }) {
                Icon(painter = icon, contentDescription = null)
            }
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MdtTheme.color.primary,
            selectedTextColor = MdtTheme.color.primary,
            indicatorColor = MdtTheme.color.primary,
            unselectedIconColor = MdtTheme.color.onSurfaceVariant,
            unselectedTextColor = MdtTheme.color.onSurfaceVariant,
        ),
        modifier = modifier,
    )
}