package com.twofasapp.feature.home.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.android.navigation.Navigator
import com.twofasapp.android.navigation.Screen
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.settings.OptionEntry
import com.twofasapp.core.design.feature.settings.OptionHeader
import com.twofasapp.core.design.feature.settings.OptionHeaderContentPaddingFirst
import com.twofasapp.core.design.foundation.lazy.listItem
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.ktx.currentActivity
import com.twofasapp.core.design.ktx.openSafely
import com.twofasapp.locale.MdtLocale
import org.koin.compose.koinInject

@Composable
fun SettingsRoute() {
    SettingsScreen()
}

@Composable
private fun SettingsScreen(
    navigator: Navigator = koinInject(),
) {
    Content(
        navigator = navigator,
    )
}

@Composable
private fun Content(
    navigator: Navigator,
) {
    val strings = MdtLocale.strings
    val activity = LocalContext.currentActivity
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                content = { Text(text = strings.settingsSettings, style = MdtTheme.typo.xl2.medium) },
                showBackButton = false,
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MdtTheme.color.background)
                .padding(top = padding.calculateTopPadding()),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            // Preferences section
            listItem(SettingsListItem.Header(strings.settingsPreferences)) {
                OptionHeader(
                    text = strings.settingsPreferences,
                    contentPadding = OptionHeaderContentPaddingFirst,
                )
            }

            listItem(SettingsListItem.Entry(strings.settingsSecurity)) {
                OptionEntry(
                    title = strings.settingsSecurity,
                    subtitle = strings.settingsSecurityDesc,
                    icon = MdtIcons.Security,
                    onClick = { navigator.open(Screen.Developer) },
                )
            }

            listItem(SettingsListItem.Entry(strings.settingsAppearance)) {
                OptionEntry(
                    title = strings.settingsAppearance,
                    subtitle = strings.settingsAppearanceDesc,
                    icon = MdtIcons.Eye,
                    onClick = { navigator.open(Screen.Customization) },
                )
            }

            listItem(SettingsListItem.Entry(strings.settingsRemovedItems)) {
                OptionEntry(
                    title = strings.settingsRemovedItems,
                    subtitle = strings.settingsRemovedItemsDesc,
                    icon = MdtIcons.Delete,
                    onClick = { navigator.open(Screen.Developer) },
                )
            }

            // Backup and Transfer section
            listItem(SettingsListItem.Header(strings.settingsBackupAndTransfer)) {
                OptionHeader(
                    text = strings.settingsBackupAndTransfer,
                )
            }

            listItem(SettingsListItem.Entry(strings.settingsBackup)) {
                OptionEntry(
                    title = strings.settingsBackup,
                    subtitle = strings.settingsBackupDesc,
                    icon = MdtIcons.CloudUpload,
                    onClick = { navigator.open(Screen.Developer) },
                )
            }

            listItem(SettingsListItem.Entry(strings.settingsExternalImport)) {
                OptionEntry(
                    title = strings.settingsExternalImport,
                    subtitle = strings.settingsImportTokensDesc,
                    icon = MdtIcons.Download,
                    onClick = { navigator.open(Screen.Developer) },
                )
            }

            // Browser Extension section
            listItem(SettingsListItem.Header(strings.settingsBrowserExtHeader)) {
                OptionHeader(
                    text = strings.settingsBrowserExtHeader,
                )
            }

            listItem(SettingsListItem.Entry(strings.settingsConnectedExtensions)) {
                OptionEntry(
                    title = strings.settingsConnectedExtensions,
                    subtitle = strings.settingsConnectedExtensionsDesc,
                    icon = MdtIcons.Extension,
                    onClick = { navigator.open(Screen.Developer) },
                )
            }

            // More section
            listItem(SettingsListItem.Header(strings.settingsMore)) {
                OptionHeader(
                    text = strings.settingsMore,
                )
            }

            listItem(SettingsListItem.Entry(strings.settingsAbout)) {
                OptionEntry(
                    title = strings.settingsAbout,
                    subtitle = strings.settingsAboutDesc,
                    icon = MdtIcons.Info,
                    onClick = { navigator.open(Screen.Developer) },
                )
            }

            listItem(SettingsListItem.Entry(strings.settingsSupport)) {
                OptionEntry(
                    title = strings.settingsSupport,
                    subtitle = strings.settingsSupportDesc,
                    icon = MdtIcons.Support,
                    external = true,
                    onClick = { uriHandler.openSafely(MdtLocale.links.support, activity) },
                )
            }

            listItem(SettingsListItem.Entry(strings.settingsDonate)) {
                OptionEntry(
                    title = strings.settingsDonate,
                    subtitle = strings.settingsDonateDesc,
                    icon = MdtIcons.Favorite,
                    external = true,
                    onClick = { uriHandler.openSafely(MdtLocale.links.donate, activity) },
                )
            }

            // Social links
            listItem(SettingsListItem.Entry("Social")) {
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

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        Content(
            navigator = Navigator.Stub,
        )
    }
}