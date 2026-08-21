package com.twofasapp.feature.about.ui.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ShareCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.android.navigation.Navigator
import com.twofasapp.android.navigation.Screen
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.settings.OptionEntry
import com.twofasapp.core.design.feature.settings.OptionHeader
import com.twofasapp.core.design.feature.settings.OptionSwitch
import com.twofasapp.core.design.foundation.other.Space
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.ktx.currentActivity
import com.twofasapp.core.design.ktx.openSafely
import com.twofasapp.locale.MdtLocale
import com.twofasapp.locale.R
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
internal fun AboutScreen(
    viewModel: AboutViewModel = koinViewModel(),
    navigator: Navigator = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScreenContent(
        uiState = uiState,
        onLicensesClick = { navigator.open(Screen.AboutLicenses) },
        onSendCrashLogsToggle = { viewModel.toggleSendCrashLogs() },
    )
}

@Composable
private fun ScreenContent(
    uiState: AboutUiState,
    onLicensesClick: () -> Unit,
    onSendCrashLogsToggle: () -> Unit,
) {
    val activity = LocalContext.currentActivity
    val uriHandler = LocalUriHandler.current
    val shareText = MdtLocale.strings.aboutTellFriendShareText

    Scaffold(
        topBar = { TopAppBar(title = MdtLocale.strings.aboutTitle) },
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = com.twofasapp.core.design.R.drawable.logo_2fas),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                    )
                    Space(16.dp)

                    Text(
                        text = "2FAS Auth",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        style = MdtTheme.typo.xl2.semiBold,
                    )

                    Space(8.dp)
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.settings__version, ""))
                            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = MdtTheme.color.onSurface)) {
                                append(" ")
                                append(uiState.version)
                            }
                        },
                        textAlign = TextAlign.Center,
                        color = MdtTheme.color.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        style = MdtTheme.typo.sm.normal,
                    )
                }
            }

            item { OptionHeader(text = MdtLocale.strings.aboutGeneral) }

            item {
                OptionEntry(
                    title = MdtLocale.strings.aboutWriteReview,
                    icon = MdtIcons.Write,
                    external = true,
                    onClick = { uriHandler.openSafely(MdtLocale.links.playStore) },
                )
            }

            item {
                OptionEntry(
                    title = MdtLocale.strings.aboutPrivacyPolicy,
                    icon = MdtIcons.LockOpen,
                    external = true,
                    onClick = { uriHandler.openSafely(MdtLocale.links.privacyPolicy) },
                )
            }

            item {
                OptionEntry(
                    title = MdtLocale.strings.aboutTerms,
                    icon = MdtIcons.Terms,
                    external = true,
                    onClick = { uriHandler.openSafely(MdtLocale.links.terms) },
                )
            }

            item {
                OptionEntry(
                    title = MdtLocale.strings.aboutLicenses,
                    icon = MdtIcons.Licenses,
                    onClick = { onLicensesClick() },
                )
            }

            item { OptionHeader(text = MdtLocale.strings.aboutShare) }

            item {
                OptionEntry(
                    title = MdtLocale.strings.aboutTellFriend,
                    icon = MdtIcons.Share,
                    onClick = {
                        ShareCompat.IntentBuilder(activity)
                            .setType("text/plain")
                            .setChooserTitle("Share 2FAS")
                            .setText(shareText)
                            .startChooser()
                    },
                )
            }

            item { OptionHeader(text = MdtLocale.strings.aboutSocialMedia) }

            item {
                OptionEntry(
                    title = MdtLocale.strings.aboutSocialDiscord,
                    image = painterResource(id = com.twofasapp.core.design.R.drawable.ic_discord),
                    external = true,
                    onClick = { uriHandler.openSafely(MdtLocale.links.discord) },
                )
            }

            item {
                OptionEntry(
                    title = MdtLocale.strings.aboutSocialYouTube,
                    image = painterResource(id = com.twofasapp.core.design.R.drawable.ic_youtube),
                    external = true,
                    onClick = { uriHandler.openSafely(MdtLocale.links.youtube) },
                )
            }

            item {
                OptionEntry(
                    title = MdtLocale.strings.aboutSocialTwitter,
                    image = painterResource(id = com.twofasapp.core.design.R.drawable.ic_twitter),
                    external = true,
                    onClick = { uriHandler.openSafely(MdtLocale.links.twitter) },
                )
            }

            item {
                OptionEntry(
                    title = MdtLocale.strings.aboutSocialGitHub,
                    image = painterResource(id = com.twofasapp.core.design.R.drawable.ic_github),
                    external = true,
                    onClick = { uriHandler.openSafely(MdtLocale.links.github) },
                )
            }

            item {
                OptionEntry(
                    title = MdtLocale.strings.aboutSocialLinkedIn,
                    image = painterResource(id = com.twofasapp.core.design.R.drawable.ic_linkedin),
                    external = true,
                    onClick = { uriHandler.openSafely(MdtLocale.links.linkedin) },
                )
            }

            item {
                OptionEntry(
                    title = MdtLocale.strings.aboutSocialReddit,
                    image = painterResource(id = com.twofasapp.core.design.R.drawable.ic_reddit),
                    external = true,
                    onClick = { uriHandler.openSafely(MdtLocale.links.reddit) },
                )
            }

            item {
                OptionEntry(
                    title = MdtLocale.strings.aboutSocialFacebook,
                    image = painterResource(id = com.twofasapp.core.design.R.drawable.ic_facebook),
                    external = true,
                    onClick = { uriHandler.openSafely(MdtLocale.links.facebook) },
                )
            }

            item { OptionHeader(text = MdtLocale.strings.aboutSendCrashes) }

            item {
                OptionSwitch(
                    title = MdtLocale.strings.settingsSendCrashes,
                    subtitle = MdtLocale.strings.settingsSendCrashesBody,
                    icon = MdtIcons.Settings,
                    checked = uiState.crashLogsEnabled,
                    onToggle = { onSendCrashLogsToggle() },
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        ScreenContent(
            uiState = AboutUiState(
                version = "1.0.0",
            ),
            onLicensesClick = {},
            onSendCrashLogsToggle = {},
        )
    }
}