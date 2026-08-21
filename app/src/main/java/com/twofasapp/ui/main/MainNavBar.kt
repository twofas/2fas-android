package com.twofasapp.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.twofasapp.android.navigation.Screen
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.locale.MdtLocale

@Composable
internal fun MainNavBar(
    currentDestination: Screen?,
    onTabSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MdtTheme.color.surfaceContainer,
        )

        NavigationBar(
            containerColor = MdtTheme.color.background,
        ) {
            MainBottomBarItem(
                selected = currentDestination == Screen.Services,
                onClick = { onTabSelected(Screen.Services) },
                icon = if (currentDestination == Screen.Services) MdtIcons.HomeFilled else MdtIcons.Home,
                label = MdtLocale.strings.bottomBarTokens,
            )

            MainBottomBarItem(
                selected = currentDestination == Screen.Settings,
                onClick = { onTabSelected(Screen.Settings) },
                icon = if (currentDestination == Screen.Settings) MdtIcons.SettingsFilled else MdtIcons.Settings,
                label = MdtLocale.strings.bottomBarSettings,
            )
        }
    }
}

@Composable
private fun RowScope.MainBottomBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: Painter,
    label: String,
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
            Icon(
                painter = icon,
                contentDescription = null,
            )
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