package com.twofasapp.feature.home.navigation

import androidx.compose.runtime.Composable
import com.twofasapp.feature.home.ui.bottombar.BottomBarListener
import com.twofasapp.feature.home.ui.services.ServicesRoute
import com.twofasapp.feature.home.ui.settings.SettingsRoute

@Composable
fun ServicesEntry(
    listener: HomeNavigationListener,
    bottomBarListener: BottomBarListener,
    showBottomBar: Boolean = true,
) {
    ServicesRoute(
        listener = listener,
        bottomBarListener = bottomBarListener,
        showBottomBar = showBottomBar,
    )
}

@Composable
fun SettingsEntry(
    listener: HomeNavigationListener,
    bottomBarListener: BottomBarListener,
    showBottomBar: Boolean = true,
) {
    SettingsRoute(
        listener = listener,
        bottomBarListener = bottomBarListener,
        showBottomBar = showBottomBar,
    )
}