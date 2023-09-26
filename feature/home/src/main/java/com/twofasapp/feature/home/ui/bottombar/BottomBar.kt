package com.twofasapp.feature.home.ui.bottombar

import androidx.compose.runtime.Composable
import com.twofasapp.android.navigation.Screen
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.common.TwNavigationBar
import com.twofasapp.designsystem.common.TwNavigationBarItem
import com.twofasapp.locale.TwLocale

private val bottomNavItems
    @Composable
    get() = listOf(
        BottomNavItem(
            title = TwLocale.strings.bottomBarTokens,
            icon = TwIcons.Home,
            iconSelected = TwIcons.HomeFilled,
            route = Screen.Services.route,
        ),
        BottomNavItem(
            title = TwLocale.strings.bottomBarSettings,
            icon = TwIcons.Settings,
            iconSelected = TwIcons.SettingsFilled,
            route = Screen.Settings.route,
        ),
    )

interface BottomBarListener {
    fun openHome()
    fun openSettings()
}

@Composable
internal fun BottomBar(
    selectedIndex: Int,
    listener: BottomBarListener,
) {
    TwNavigationBar {
        bottomNavItems.forEachIndexed { index, item ->
            TwNavigationBarItem(
                text = item.title,
                icon = if (index == selectedIndex) item.iconSelected else item.icon,
                selected = index == selectedIndex,
                showBadge = false,
                onClick = {
                    when {
                        index == 0 && selectedIndex != 0 -> listener.openHome()
                        index == 1 && selectedIndex != 1 -> listener.openSettings()
                    }
                }
            )
        }
    }
}
