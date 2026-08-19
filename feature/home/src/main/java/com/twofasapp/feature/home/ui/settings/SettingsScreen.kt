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
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.settings.SettingsDivider
import com.twofasapp.core.design.feature.settings.SettingsLink
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.ktx.openSafely
import com.twofasapp.feature.home.navigation.HomeNavigationListener
import com.twofasapp.feature.home.ui.bottombar.BottomBar
import com.twofasapp.feature.home.ui.bottombar.BottomBarListener
import com.twofasapp.locale.MdtLocale

@Composable
internal fun SettingsRoute(
    listener: HomeNavigationListener,
    bottomBarListener: BottomBarListener,
    showBottomBar: Boolean = true,
) {
    SettingsScreen(
        listener = listener,
        bottomBarListener = bottomBarListener,
        showBottomBar = showBottomBar,
    )
}

@Composable
private fun SettingsScreen(
    listener: HomeNavigationListener,
    bottomBarListener: BottomBarListener,
    showBottomBar: Boolean = true,
) {
    val activity = LocalContext.current as Activity
    val uriHandler = LocalUriHandler.current

    Scaffold(
        bottomBar = { if (showBottomBar) BottomBar(1, bottomBarListener) },
        topBar = { TopAppBar(title = MdtLocale.strings.settingsSettings, showBackButton = false) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MdtTheme.color.background)
                .padding(padding),
        ) {
            item {
                SettingsLink(title = MdtLocale.strings.settingsBackup, icon = MdtIcons.CloudUpload) {
                    listener.openBackup(false)
                }
            }

            item {
                SettingsLink(title = MdtLocale.strings.settingsSecurity, icon = MdtIcons.Security) {
                    listener.openSecurity(activity)
                }
            }

            item {
                SettingsLink(title = MdtLocale.strings.settingsAppearance, icon = MdtIcons.Eye) {
                    listener.openAppSettings()
                }
            }

            item {
                SettingsLink(title = MdtLocale.strings.settingsExternalImport, icon = MdtIcons.Download) {
                    listener.openExternalImport()
                }
            }

            item {
                SettingsLink(title = MdtLocale.strings.settingsBrowserExt, icon = MdtIcons.Extension) {
                    listener.openBrowserExt()
                }
            }

            item { SettingsDivider() }

            item {
                SettingsLink(title = MdtLocale.strings.settingsTrash, icon = MdtIcons.Delete) {
                    listener.openTrash()
                }
            }

            item {
                SettingsLink(title = MdtLocale.strings.settingsSupport, icon = MdtIcons.Support, external = true) {
                    uriHandler.openSafely(MdtLocale.links.support, activity)
                }
            }

            item {
                SettingsLink(title = MdtLocale.strings.settingsAbout, icon = MdtIcons.Info) {
                    listener.openAbout()
                }
            }

            item { SettingsDivider() }

            item {
                SettingsLink(title = MdtLocale.strings.settingsDonate, icon = MdtIcons.Favorite, external = true) {
                    uriHandler.openSafely(MdtLocale.links.donate, activity)
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
                        painter = painterResource(id = com.twofasapp.core.design.R.drawable.ic_discord),
                        contentDescription = null,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .clickable { uriHandler.openSafely(MdtLocale.links.discord, activity) }
                            .padding(14.dp),
                    )
                    Image(
                        painter = painterResource(id = com.twofasapp.core.design.R.drawable.ic_youtube),
                        contentDescription = null,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .clickable { uriHandler.openSafely(MdtLocale.links.youtube, activity) }
                            .padding(14.dp),
                    )
                    Image(
                        painter = painterResource(id = com.twofasapp.core.design.R.drawable.ic_twitter),
                        contentDescription = null,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .clickable { uriHandler.openSafely(MdtLocale.links.twitter, activity) }
                            .padding(14.dp),
                    )
                    Icon(
                        painter = painterResource(id = com.twofasapp.core.design.R.drawable.ic_github),
                        contentDescription = null,
                        tint = MdtTheme.color.onSurface,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .clickable { uriHandler.openSafely(MdtLocale.links.github, activity) }
                            .padding(14.dp),
                    )
                }
            }
        }
    }
}