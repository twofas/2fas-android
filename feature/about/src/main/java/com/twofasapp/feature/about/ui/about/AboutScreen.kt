package com.twofasapp.feature.about.ui.about

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ShareCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.ktx.openSafely
import com.twofasapp.designsystem.settings.SettingsDivider
import com.twofasapp.designsystem.settings.SettingsHeader
import com.twofasapp.designsystem.settings.SettingsLink
import com.twofasapp.designsystem.settings.SettingsSwitch
import com.twofasapp.locale.R
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AboutRoute(
    openLicenses: () -> Unit,
    viewModel: AboutViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AboutScreen(
        uiState = uiState,
        versionName = viewModel.versionName,
        onLicensesClick = openLicenses,
        onReviewClick = { viewModel.reviewDone() },
        onSendCrashLogsToggle = { viewModel.toggleSendCrashLogs() }
    )
}

@Composable
private fun AboutScreen(
    uiState: AboutUiState,
    versionName: String,
    onLicensesClick: () -> Unit,
    onReviewClick: () -> Unit,
    onSendCrashLogsToggle: () -> Unit,
) {
    val activity = LocalContext.current as Activity
    val uriHandler = LocalUriHandler.current
    val shareText = TwLocale.strings.aboutTellFriendShareText

    Scaffold(
        topBar = { TwTopAppBar(titleText = TwLocale.strings.aboutTitle) }
    ) { padding ->
        Column(modifier = Modifier.padding(top = padding.calculateTopPadding())) {

            LazyColumn(modifier = Modifier.weight(1f)) {

                item { SettingsHeader(title = TwLocale.strings.aboutGeneral) }

                item {
                    SettingsLink(title = TwLocale.strings.aboutWriteReview, icon = TwIcons.Write, external = true) {
                        uriHandler.openSafely(TwLocale.links.playStore, activity)
                    }
                }

                item {
                    SettingsLink(title = TwLocale.strings.aboutPrivacyPolicy, icon = TwIcons.LockOpen, external = true) {
                        uriHandler.openSafely(TwLocale.links.privacyPolicy, activity)
                    }
                }

                item {
                    SettingsLink(title = TwLocale.strings.aboutTerms, icon = TwIcons.Terms, external = true) {
                        uriHandler.openSafely(TwLocale.links.terms, activity)
                    }
                }

                item {
                    SettingsLink(title = TwLocale.strings.aboutLicenses, icon = TwIcons.Licenses) {
                        onLicensesClick()
                    }
                }

                item { SettingsDivider() }

                item { SettingsHeader(title = TwLocale.strings.aboutShare) }

                item {
                    SettingsLink(title = TwLocale.strings.aboutTellFriend, icon = TwIcons.Share) {
                        ShareCompat.IntentBuilder(activity)
                            .setType("text/plain")
                            .setChooserTitle("Share 2FAS")
                            .setText(shareText)
                            .startChooser()
                    }
                }

                item { SettingsDivider() }

                item { SettingsHeader(title = TwLocale.strings.aboutSendCrashes) }

                item {
                    SettingsSwitch(
                        title = TwLocale.strings.settingsSendCrashes,
                        subtitle = TwLocale.strings.settingsSendCrashesBody,
                        icon = TwIcons.Settings,
                        checked = uiState.appSettings.sendCrashLogs,
                        onCheckedChange = { onSendCrashLogsToggle() }
                    )
                }

                item { SettingsDivider() }

                item { SettingsHeader(title = TwLocale.strings.aboutSocialMedia) }

                item {
                    SettingsLink(
                        title = TwLocale.strings.aboutSocialDiscord,
                        image = painterResource(id = com.twofasapp.designsystem.R.drawable.ic_discord),
                        external = true
                    ) {
                        uriHandler.openSafely(TwLocale.links.discord, activity)
                    }
                }

                item {
                    SettingsLink(
                        title = TwLocale.strings.aboutSocialYouTube,
                        image = painterResource(id = com.twofasapp.designsystem.R.drawable.ic_youtube),
                        external = true
                    ) {
                        uriHandler.openSafely(TwLocale.links.youtube, activity)
                    }
                }

                item {
                    SettingsLink(
                        title = TwLocale.strings.aboutSocialTwitter,
                        image = painterResource(id = com.twofasapp.designsystem.R.drawable.ic_twitter),
                        external = true
                    ) {
                        uriHandler.openSafely(TwLocale.links.twitter, activity)
                    }
                }

                item {
                    SettingsLink(
                        title = TwLocale.strings.aboutSocialGitHub,
                        image = painterResource(id = com.twofasapp.designsystem.R.drawable.ic_github),
                        external = true
                    ) {
                        uriHandler.openSafely(TwLocale.links.github, activity)
                    }
                }

                item {
                    SettingsLink(
                        title = TwLocale.strings.aboutSocialLinkedIn,
                        image = painterResource(id = com.twofasapp.designsystem.R.drawable.ic_linkedin),
                        external = true
                    ) {
                        uriHandler.openSafely(TwLocale.links.linkedin, activity)
                    }
                }

                item {
                    SettingsLink(
                        title = TwLocale.strings.aboutSocialReddit,
                        image = painterResource(id = com.twofasapp.designsystem.R.drawable.ic_reddit),
                        external = true
                    ) {
                        uriHandler.openSafely(TwLocale.links.reddit, activity)
                    }
                }

                item {
                    SettingsLink(
                        title = TwLocale.strings.aboutSocialFacebook,
                        image = painterResource(id = com.twofasapp.designsystem.R.drawable.ic_facebook),
                        external = true
                    ) {
                        uriHandler.openSafely(TwLocale.links.facebook, activity)
                    }
                }

                item { SettingsDivider() }

                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                            .padding(start = 24.dp, end = 16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.settings__version, versionName),
                            color = TwTheme.color.onSurfaceSecondary,
                            style = TwTheme.typo.body3
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Image(
                            painter = painterResource(id = com.twofasapp.designsystem.R.drawable.logo_2fas),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
