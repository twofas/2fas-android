package com.twofasapp.feature.home.ui.settings

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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

@Composable
internal fun SettingsRoute(
    listener: HomeNavigationListener,
    bottomBarListener: BottomBarListener,
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
        topBar = { TwTopAppBar(titleText = TwLocale.strings.settingsSettings, showBackButton = false) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(TwTheme.color.background)
                .padding(padding)
        ) {

            item {
                SettingsLink(title = TwLocale.strings.settingsBackup, icon = TwIcons.CloudUpload) {
                    listener.openBackup(false)
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

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
                ) {
                    Image(
                        painter = painterResource(id = com.twofasapp.designsystem.R.drawable.ic_discord),
                        contentDescription = null,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .clickable { uriHandler.openSafely(TwLocale.links.discord, activity) }
                            .padding(14.dp),
                    )
                    Image(
                        painter = painterResource(id = com.twofasapp.designsystem.R.drawable.ic_youtube),
                        contentDescription = null,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .clickable { uriHandler.openSafely(TwLocale.links.youtube, activity) }
                            .padding(14.dp),
                    )
                    Image(
                        painter = painterResource(id = com.twofasapp.designsystem.R.drawable.ic_twitter),
                        contentDescription = null,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .clickable { uriHandler.openSafely(TwLocale.links.twitter, activity) }
                            .padding(14.dp),
                    )
                    Icon(
                        painter = painterResource(id = com.twofasapp.designsystem.R.drawable.ic_github),
                        contentDescription = null,
                        tint = TwTheme.color.onSurfacePrimary,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .clickable { uriHandler.openSafely(TwLocale.links.github, activity) }
                            .padding(14.dp),
                    )
                }
            }
        }
    }
}