package com.twofasapp.ui.main

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import com.twofasapp.android.navigation.Screen
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.locale.MdtLocale

@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
internal fun NavigationSuiteScope.mainNavigationSuiteItems(
    currentDestination: Screen?,
    onTabSelected: (Screen) -> Unit,
) {
    item(
        selected = currentDestination == Screen.Services,
        onClick = { onTabSelected(Screen.Services) },
        icon = {
            Icon(
                painter = if (currentDestination == Screen.Services) MdtIcons.HomeFilled else MdtIcons.Home,
                contentDescription = null,
            )
        },
        label = { Text(MdtLocale.strings.bottomBarTokens) },
    )
    item(
        selected = currentDestination == Screen.Settings,
        onClick = { onTabSelected(Screen.Settings) },
        icon = {
            Icon(
                painter = if (currentDestination == Screen.Settings) MdtIcons.SettingsFilled else MdtIcons.Settings,
                contentDescription = null,
            )
        },
        label = { Text(MdtLocale.strings.bottomBarSettings) },
    )
}