package com.twofasapp.feature.home.ui.settings

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.ktx.openSafely
import com.twofasapp.designsystem.settings.SettingsDivider
import com.twofasapp.designsystem.settings.SettingsLink
import com.twofasapp.feature.home.navigation.HomeNavigationListener
import com.twofasapp.feature.home.ui.bottombar.BottomBar
import com.twofasapp.feature.home.ui.bottombar.BottomBarListener
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun SettingsRoute(
    listener: HomeNavigationListener,
    bottomBarListener: BottomBarListener,
    viewModel: SettingsViewModel = koinViewModel()
) {
    SettingsScreen(
        listener = listener,
        bottomBarListener = bottomBarListener,
    )
}

@Composable
private fun SettingsScreen(
    listener: HomeNavigationListener,
    bottomBarListener: BottomBarListener,
) {
    val activity = LocalContext.current as Activity
    val uriHandler = LocalUriHandler.current

    Scaffold(
        bottomBar = { BottomBar(1, bottomBarListener) },
        topBar = { TwTopAppBar(titleText = "Settings", showBackButton = false) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(TwTheme.color.background)
                .padding(padding)
        ) {
            item {
                SettingsLink(title = TwLocale.strings.settingsBackup, icon = TwIcons.CloudUpload) {
                    listener.openBackup(activity)
                }
            }

            item {
                SettingsLink(title = TwLocale.strings.settingsSecurity, icon = TwIcons.Security) {
                    listener.openSecurity(activity)
                }
            }

            item {
                SettingsLink(title = TwLocale.strings.settingsAppearance, icon = TwIcons.Eye) {
                    listener.openAppSettings()
                }
            }

            item {
                SettingsLink(title = TwLocale.strings.settingsExternalImport, icon = TwIcons.Download) {
                    listener.openExternalImport()
                }
            }

            item {
                SettingsLink(title = TwLocale.strings.settingsBrowserExt, icon = TwIcons.Extension) {
                    listener.openBrowserExt()
                }
            }

            item { SettingsDivider() }

            item {
                SettingsLink(title = TwLocale.strings.settingsTrash, icon = TwIcons.Delete) {
                    listener.openTrash()
                }
            }

            item {
                SettingsLink(title = TwLocale.strings.settingsSupport, icon = TwIcons.Support, external = true) {
                    uriHandler.openSafely(TwLocale.links.support, activity)
                }
            }

            item {
                SettingsLink(title = TwLocale.strings.settingsAbout, icon = TwIcons.Info) {
                    listener.openAbout()
                }
            }

            item { SettingsDivider() }

            item {
                SettingsLink(title = TwLocale.strings.settingsDonate, icon = TwIcons.Favorite, external = true) {
                    uriHandler.openSafely(TwLocale.links.donate, activity)
                }
            }
        }
    }
}