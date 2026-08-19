package com.twofasapp.feature.home.ui.bottombar

import androidx.compose.runtime.Composable
import com.twofasapp.android.navigation.LegacyScreen
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.foundation.navigationbar.NavigationBar
import com.twofasapp.core.design.foundation.navigationbar.NavigationBarItem
import com.twofasapp.locale.MdtLocale

private val bottomNavItems
    @Composable
    get() = listOf(
        BottomNavItem(
            title = MdtLocale.strings.bottomBarTokens,
            icon = MdtIcons.Home,
            iconSelected = MdtIcons.HomeFilled,
            route = LegacyScreen.Services.route,
        ),
        BottomNavItem(
            title = MdtLocale.strings.bottomBarSettings,
            icon = MdtIcons.Settings,
            iconSelected = MdtIcons.SettingsFilled,
            route = LegacyScreen.Settings.route,
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