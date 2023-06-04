package com.twofasapp.feature.about.ui.about

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.app.ShareCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.locale.R
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.ktx.openSafely
import com.twofasapp.designsystem.settings.SettingsDivider
import com.twofasapp.designsystem.settings.SettingsHeader
import com.twofasapp.designsystem.settings.SettingsLink
import com.twofasapp.designsystem.settings.SettingsSwitch
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
        Column(modifier = Modifier.padding(padding)) {

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
            }

            SettingsLink(
                title = stringResource(id = R.string.settings__version, versionName),
                image = painterResource(id = com.twofasapp.designsystem.R.drawable.logo_2fas),
                textColor = TwTheme.color.onSurfaceSecondary
            )
        }
    }
}
