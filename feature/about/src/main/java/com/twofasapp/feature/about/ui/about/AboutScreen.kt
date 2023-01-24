package com.twofasapp.feature.about.ui.about

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.core.app.ShareCompat
import com.google.android.play.core.review.ReviewManagerFactory
import com.twofasapp.designsystem.R
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.settings.SettingsDivider
import com.twofasapp.designsystem.settings.SettingsHeader
import com.twofasapp.designsystem.settings.SettingsLink
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AboutRoute(
    openLicenses: () -> Unit,
    viewModel: AboutViewModel = koinViewModel()
) {
    AboutScreen(
        versionName = viewModel.versionName,
        onLicensesClick = openLicenses,
        onReviewClick = { viewModel.reviewDone() }
    )
}

@Composable
private fun AboutScreen(
    versionName: String,
    onLicensesClick: () -> Unit,
    onReviewClick: () -> Unit,
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
                    SettingsLink(title = TwLocale.strings.aboutWriteReview, icon = TwIcons.Write) {
                        val manager = ReviewManagerFactory.create(activity)
                        val request = manager.requestReviewFlow()
                        request.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val flow = manager.launchReviewFlow(
                                    activity,
                                    task.result
                                )
                                flow.addOnCompleteListener { onReviewClick() }
                            }
                        }
                    }
                }

                item {
                    SettingsLink(title = TwLocale.strings.aboutPrivacyPolicy, icon = TwIcons.LockOpen) {
                        uriHandler.openUri(TwLocale.links.privacyPolicy)
                    }
                }

                item {
                    SettingsLink(title = TwLocale.strings.aboutTerms, icon = TwIcons.Terms) {
                        uriHandler.openUri(TwLocale.links.terms)
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
            }

            SettingsLink(
                title = "Version $versionName",
                image = painterResource(id = R.drawable.logo_2fas),
                textColor = TwTheme.color.onSurfaceSecondary
            )
        }
    }
}
