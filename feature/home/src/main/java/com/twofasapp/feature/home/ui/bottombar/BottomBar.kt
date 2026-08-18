package com.twofasapp.feature.home.ui.bottombar

import androidx.compose.runtime.Composable
import com.twofasapp.android.navigation.Screen
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.foundation.navigationbar.NavigationBar
import com.twofasapp.core.design.foundation.navigationbar.NavigationBarItem
import com.twofasapp.locale.TwLocale

private val bottomNavItems
    @Composable
    get() = listOf(
        BottomNavItem(
            title = TwLocale.strings.bottomBarTokens,
            icon = MdtIcons.Home,
            iconSelected = MdtIcons.HomeFilled,
            route = Screen.Services.route,
        ),
        BottomNavItem(
            title = TwLocale.strings.bottomBarSettings,
            icon = MdtIcons.Settings,
            iconSelected = MdtIcons.SettingsFilled,
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
    onItemClick: () -> Unit = {},
) {
    NavigationBar {
        bottomNavItems.forEachIndexed { index, item ->
            NavigationBarItem(
                text = item.title,
                icon = if (index == selectedIndex) item.iconSelected else item.icon,
                selected = index == selectedIndex,
                showBadge = false,
                onClick = {
                    when {
                        index == 0 && selectedIndex != 0 -> {
                            onItemClick()
                            listener.openHome()
                        }
                        index == 1 && selectedIndex != 1 -> {
                            onItemClick()
                            listener.openSettings()
                        }
                    }
                },
            )
        }
    }
}