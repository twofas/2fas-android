package com.twofasapp.feature.home.ui.bottombar

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.common.TwNavigationBar
import com.twofasapp.designsystem.common.TwNavigationBarItem
import com.twofasapp.feature.home.navigation.HomeNode
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

private val bottomNavItems
    @Composable
    get() = listOf(
        BottomNavItem(
            title = TwLocale.strings.bottomBarTokens,
            icon = TwIcons.Home,
            iconSelected = TwIcons.HomeFilled,
            route = HomeNode.Services.route,
        ),
        BottomNavItem(
            title = TwLocale.strings.bottomBarSettings,
            icon = TwIcons.Settings,
            iconSelected = TwIcons.SettingsFilled,
            route = HomeNode.Settings.route,
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
    viewModel: BottomBarViewModel = koinViewModel(),
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

internal class BottomBarViewModel() : ViewModel()