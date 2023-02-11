package com.twofasapp.feature.home.ui.bottombar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.twofasapp.data.notifications.NotificationsRepository
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.common.TwNavigationBar
import com.twofasapp.designsystem.common.TwNavigationBarItem
import com.twofasapp.feature.home.navigation.HomeNode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.androidx.compose.koinViewModel

private val bottomNavItems
    @Composable
    get() = listOf(
        BottomNavItem(
            title = "Tokens",
            icon = TwIcons.Home,
            iconSelected = TwIcons.HomeFilled,
            route = HomeNode.Services.route,
        ),
        BottomNavItem(
            title = "Settings",
            icon = TwIcons.Settings,
            iconSelected = TwIcons.SettingsFilled,
            route = HomeNode.Settings.route,
        ),
        BottomNavItem(
            title = "Notifications",
            icon = TwIcons.Notification,
            iconSelected = TwIcons.NotificationFilled,
            route = HomeNode.Notifications.route,
        ),
    )

interface BottomBarListener {
    fun openHome()
    fun openSettings()
    fun openNotifications()
}

@Composable
internal fun BottomBar(
    selectedIndex: Int,
    listener: BottomBarListener,
    viewModel: BottomBarViewModel = koinViewModel(),
) {
    val hasUnreadNotifications by viewModel.hasUnreadNotifications.collectAsStateWithLifecycle()

    TwNavigationBar {
        bottomNavItems.forEachIndexed { index, item ->
            TwNavigationBarItem(
                text = item.title,
                icon = if (index == selectedIndex) item.iconSelected else item.icon,
                selected = index == selectedIndex,
                showBadge = index == 2 && hasUnreadNotifications,
                onClick = {
                    when {
                        index == 0 && selectedIndex != 0 -> listener.openHome()
                        index == 1 && selectedIndex != 1 -> listener.openSettings()
                        index == 2 && selectedIndex != 2 -> listener.openNotifications()
                    }
                }
            )
        }
    }
}

internal class BottomBarViewModel(
    notificationsRepository: NotificationsRepository,
) : ViewModel() {

    val hasUnreadNotifications = notificationsRepository.hasUnreadNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)
}