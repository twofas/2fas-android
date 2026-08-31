package com.twofasapp.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.twofasapp.android.navigation.Screen
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.other.DotBadge
import com.twofasapp.core.design.foundation.outline.HorizontalLine
import com.twofasapp.locale.MdtLocale

@Composable
internal fun MainNavBar(
    currentDestination: Screen?,
    showBackupError: Boolean,
    onTabSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HorizontalLine(
            modifier = Modifier.fillMaxWidth(),
            color = MdtTheme.color.surfaceContainer,
        )

        NavigationBar(
            containerColor = MdtTheme.color.background,
        ) {
            NavBarItem(
                selected = currentDestination == Screen.Home,
                onClick = { onTabSelected(Screen.Home) },
                icon = if (currentDestination == Screen.Home) MdtIcons.HomeFilled else MdtIcons.Home,
                label = MdtLocale.strings.bottomBarTokens,
            )

            NavBarItem(
                selected = currentDestination == Screen.Settings,
                onClick = { onTabSelected(Screen.Settings) },
                icon = if (currentDestination == Screen.Settings) MdtIcons.SettingsFilled else MdtIcons.Settings,
                label = MdtLocale.strings.bottomBarSettings,
                showBadge = showBackupError,
            )
        }
    }
}

@Composable
private fun RowScope.NavBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: Painter,
    label: String,
    showBadge: Boolean = false,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MdtTheme.typo.xs3.semiBold.copy(color = Color.Unspecified),
            )
        },
        icon = {
            BadgedBox(
                badge = {
                    if (showBadge) {
                        DotBadge(modifier = Modifier.offset(x = 8.dp, y = (-7).dp))
                    }
                },
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                )
            }
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MdtTheme.color.onSecondaryContainer,
            selectedTextColor = MdtTheme.color.onSurface,
            indicatorColor = MdtTheme.color.secondaryContainer,
            unselectedIconColor = MdtTheme.color.onSurfaceVariant,
            unselectedTextColor = MdtTheme.color.onSurfaceVariant,
        ),
    )
}